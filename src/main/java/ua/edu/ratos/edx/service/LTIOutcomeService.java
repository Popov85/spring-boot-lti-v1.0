package ua.edu.ratos.edx.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth.common.signature.SignatureSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthClientHttpRequestFactory;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.stereotype.Service;
import ua.edu.ratos.edx.security.lti.LTIOutcomeParams;
import ua.edu.ratos.edx.security.lti.LTIUserConsumerCredentials;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LTIOutcomeService {
	
	private static final Log LOG = LogFactory.getLog(LTIOutcomeService.class);
		
	@Autowired
	private XMLScoreRequestBodyBuilder xmlScoreRequestBodyBuilder;

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
            LOG.warn("Outcome parameters are not included, result is not sent to LMS :: "+ score);
            return;
        }
        // Create a client-secret-aware rest template for posting score to LMS
        BaseProtectedResourceDetails resourceDetails = new BaseProtectedResourceDetails();
        resourceDetails.setConsumerKey(principal.getConsumerKey());
        resourceDetails.setSharedSecret((SignatureSecret) authentication.getCredentials()); 
        OAuthRestTemplate authRestTemplate = new OAuthRestTemplate(resourceDetails);

        String sourcedId = outcome.get().getSourcedId();
        String outcomeURL = outcome.get().getOutcomeURL();
        
        String newOutcomeURL = outcomeURL.replaceAll("https", "http");
      
        // Just to keep things simple, create a value of milliseconds since 1970
        String messageIdentifier = Long.toString(new Date().getTime());
        String textScore = Double.toString(score);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        String body = xmlScoreRequestBodyBuilder.getEnvelopeRequestBody(sourcedId, messageIdentifier, textScore);
               
        // Add an additional parameter, specifically: oauth_body_hash
        OAuthClientHttpRequestFactory requestFactory = (OAuthClientHttpRequestFactory) authRestTemplate.getRequestFactory();
        Map<String, String> additionalOAuthParameters = new HashMap<>();
        String hash = doXMLBodyHashSHA1(body);
        additionalOAuthParameters.put("oauth_body_hash", hash);
        requestFactory.setAdditionalOAuthParameters(additionalOAuthParameters);
        
        LOG.debug("oauth_body_hash:: "+hash);

        HttpEntity<String> request = new HttpEntity<String>(body, headers);
        
        String result = authRestTemplate.postForObject(new URI(newOutcomeURL),request, String.class);

        LOG.debug("Result  :: "+ textScore+", sent with message :: "+ result);
    }

    /**
     * As per LTI v 1.1.1 specification the request's XML body has to be hashed with SHA-1;
     * The oauth_body_hash [OBH, 11] is computed using a SHA-1 hash of the body contents and added to the Authorization header.
     * @param body
     * @return
     * @throws NoSuchAlgorithmException
     */
	private String doXMLBodyHashSHA1(String body) throws NoSuchAlgorithmException {
		String algorithmCode = "SHA-1";
		MessageDigest md = MessageDigest.getInstance(algorithmCode);
		md.update(body.getBytes());
		byte[] output = Base64.encodeBase64(md.digest());
        String hash = new String(output);
		return hash;
	}

}
