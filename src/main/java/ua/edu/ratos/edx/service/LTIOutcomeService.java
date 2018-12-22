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
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LTIOutcomeService {
	
	private static final Log LOG = LogFactory.getLog(LTIOutcomeService.class);
	
	 private static final String EDX_COURSE_POST_LINK_MOCK = "http://localhost:8090/ratos/receive";
	
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
        
        String newOutcomeURL = outcomeURL.replaceAll("https", "http");
        
        LOG.debug("URL :: "+newOutcomeURL);

        // Just to keep things simple, create a value of milliseconds since 1970
        String messageIdentifier = Long.toString(new Date().getTime());
        String textScore = Double.toString(score);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        String body = xmlScoreRequestBodyBuilder.getEnvelopeRequestBodyAsString(sourcedId, messageIdentifier, textScore);
        
        
        /*MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(body.getBytes());
        byte[] output = Base64.getEncoder().encode(md.digest());*/
        
        MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(body.getBytes());
		byte[] output = Base64.encodeBase64(md.digest());

        String hash = new String(output);
       
        
        
        OAuthClientHttpRequestFactory requestFactory = (OAuthClientHttpRequestFactory) authRestTemplate.getRequestFactory();
        Map<String, String> additionalOAuthParameters = new HashMap<>();
        additionalOAuthParameters.put("oauth_body_hash", URLEncoder.encode(hash, "UTF-8"));
        requestFactory.setAdditionalOAuthParameters(additionalOAuthParameters);
        

        HttpEntity<String> request = new HttpEntity<String>(body, headers);
        
        LOG.debug("Try to post score... "+ score);


        //ResponseEntity<String> result = authRestTemplate.postForEntity(new URI(newOutcomeURL),request, String.class);
       
        String result = authRestTemplate.postForObject(new URI(newOutcomeURL),request, String.class);


        LOG.debug("Result  :: "+ textScore+", sent with status code :: "+ result);
    }

   
}
