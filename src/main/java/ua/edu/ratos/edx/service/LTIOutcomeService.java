package ua.edu.ratos.edx.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth.common.signature.SignatureSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.stereotype.Service;
import ua.edu.ratos.edx.security.lti.LTIOutcomeParams;
import ua.edu.ratos.edx.security.lti.LTIUserConsumerCredentials;
import ua.edu.ratos.edx.web.domain.*;
import java.net.URI;
import java.util.Date;
import java.util.Optional;

@Service
public class LTIOutcomeService {
	
	private static final Log LOG = LogFactory.getLog(LTIOutcomeService.class);

    private XmlMapper xmlMapper = new XmlMapper();

    /**
     * Sends score to Learning Management System (LMS) if it was configured to allow sending outcomes,
     * (outcome parameters were present in the initial launch request)
     * @param authentication security object containing launch request information
     * @param score score between 0-1 as per LTI v1.1.1 specification
     * @see <a href="https://www.imsglobal.org/specs/ltiv1p1p1/implementation-guide#toc-3">LTI v 1.1.1</a>
     * @throws Exception
     */
    public void sendOutcome(final Authentication authentication, final Double score) throws Exception {
        LTIUserConsumerCredentials principal = (LTIUserConsumerCredentials)authentication.getPrincipal();
        Optional<LTIOutcomeParams> outcome = principal.getOutcome();

        if (!outcome.isPresent()) {
            LOG.debug("Outcome parameters are not included, result is not sent to LMS :: "+ score);
            return;
        }

        // Create a client secret-aware rest template for posting score to LMS
        BaseProtectedResourceDetails resourceDetails = new BaseProtectedResourceDetails();
        resourceDetails.setConsumerKey(principal.getConsumerKey());
        resourceDetails.setSharedSecret((SignatureSecret) authentication.getCredentials());
        OAuthRestTemplate authRestTemplate = new OAuthRestTemplate(resourceDetails);

        String sourcedId = outcome.get().getSourcedId();
        String outcomeURL = outcome.get().getOutcomeURL();

        // Just to keep things simple, create a value of milliseconds since 1970
        String messageIdentifier = Long.toString(new Date().getTime());
        String textScore = Double.toString(score);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        IMSXPOXEnvelopeRequest envelopeRequest = getEnvelopeRequest(sourcedId, messageIdentifier, textScore);
        String body = xmlMapper.writeValueAsString(envelopeRequest);

        HttpEntity<String> request = new HttpEntity<String>(body, headers);

        ResponseEntity<String> result = authRestTemplate.postForEntity(new URI(outcomeURL),request, String.class);

        LOG.debug("Result  :: "+ score+", sent with status code :: "+ result.getStatusCode());
    }

    /**
     * Create a Java-object ready for XML serialization according to LTI v1.1.1 specification
     * @param sourcedId a value needed to posting score to LMS as per LTI specification
     * @param messageIdentifier Some value for identifying messages (in our case the number of milliseconds since 1970)
     * @param textScore score between 0-1 gained by a user after learning session completion
     * @return fully populated object ready to be XML-serialized according to LTI specification
     * @see <a href="https://www.imsglobal.org/specs/ltiv1p1p1/implementation-guide#toc-3">LTI v 1.1.1</a>
     */
    private IMSXPOXEnvelopeRequest getEnvelopeRequest(String sourcedId, String messageIdentifier, String textScore) {
        return new IMSXPOXEnvelopeRequest()
                .setIMSXPOXHeader(new IMSXPOXHeader()
                        .setImsxPOXRequestHeaderInfo(new IMSXPOXRequestHeaderInfo()
                                .setImsxMessageIdentifier(messageIdentifier)))
                .setIMSXPOXBody(new IMSXPOXBody()
                        .setReplaceResultRequest(new ReplaceResultRequest()
                                .setResultRecord(new ResultRecord()
                                        .setSourcedGUID(new SourcedGUID()
                                                .setSourcedId(sourcedId))
                                        .setResult(new Result()
                                                .setResultScore(new ResultScore()
                                                        .setTextString(textScore))))));
    }
}
