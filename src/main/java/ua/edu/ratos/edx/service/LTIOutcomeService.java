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
	private XMLRequestBodyBuilder xmlRequestBodyBuilder;
	
	@Autowired
	private LTIRetryOutcomeService ltiRetryOutcomeService;
	
	private static final boolean FIX_PROTOCOL = true;

    /**
     * Sends score to Learning Management System (LMS) if it was configured to allow sending outcomes,
     * (outcome parameters were present in the initial launch request)
     * @param authentication security object containing launch request information
     * @param score score between 0-1 as per LTI v1.1.1 specification
     * @see <a href="https://www.imsglobal.org/specs/ltiv1p1p1/implementation-guide#toc-3">LTI v 1.1.1</a>
     * @throws Exception
     */
    public void sendOutcome(final Authentication authentication, final String protocol, final Long schemeId, final Double score) throws Exception {
        LTIUserConsumerCredentials principal = (LTIUserConsumerCredentials)authentication.getPrincipal();
        Optional<LTIOutcomeParams> outcome = principal.getOutcome();
        if (!outcome.isPresent()) {
            LOG.warn("Outcome parameters are not included, result is not sent to LMS :: "+ score);
            return;
        }
        OAuthRestTemplate authRestTemplate = getOAuthRestTemplate(authentication, principal);

        String sourcedId = outcome.get().getSourcedId();
        String outcomeURL = outcome.get().getOutcomeURL();
        
        String actualOutcomeURL = new String(outcomeURL);
        
        if (FIX_PROTOCOL) actualOutcomeURL = fixProtocol(outcomeURL, protocol);
    
        URI uri = new URI(actualOutcomeURL);
      
        // Just to keep things simple, create a value of milliseconds since 1970
        String messageIdentifier = Long.toString(new Date().getTime());
        String textScore = Double.toString(score);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        String body = xmlRequestBodyBuilder.build(sourcedId, messageIdentifier, textScore);
               
        // Add additional oauth_body_hash parameter as per OAuth extension
        addAdditionalParam(authRestTemplate, body);

        HttpEntity<String> request = new HttpEntity<String>(body, headers);
        
        // try to send multiple times for sure
        String email= principal.getEmail().get();
        ltiRetryOutcomeService.doSend(authRestTemplate, uri, request, email, schemeId);

    }
	

    /**
     * Create a client-secret-aware rest template instance for posting score to LMS
     * @param authentication
     * @param principal
     * @return rest template
     */
	private OAuthRestTemplate getOAuthRestTemplate(final Authentication authentication,
			LTIUserConsumerCredentials principal) {
        BaseProtectedResourceDetails resourceDetails = new BaseProtectedResourceDetails();
        resourceDetails.setConsumerKey(principal.getConsumerKey());
        resourceDetails.setSharedSecret((SignatureSecret) authentication.getCredentials()); 
        OAuthRestTemplate authRestTemplate = new OAuthRestTemplate(resourceDetails);
		return authRestTemplate;
	}
	
	
	/**
	 * In some cases, LMS expects TP to send the outcome to a different protocol than is actually used;
	 * This service method is optional, decide if you need to use it, if not just omit it
	 * e.g. (actual HTTP & expected HTTP)
	 * @param url, like  https://open-edx.org/outcome-service
	 * @param protocol {http, https, ftp}
	 * @return fixed URL
	 */
	private String fixProtocol(String initialURL, String protocol) {
		int end = initialURL.indexOf(':');
		String initialProtocol = initialURL.substring(0, end);
		LOG.debug("Initial protocol :: "+initialProtocol);
		if (!initialProtocol.equals(protocol)) {
			// replace the initial protocol with the actual one
			String result = initialURL.replace(initialProtocol, protocol);
			LOG.debug("Fixed protocol :: "+result);
			return result;
		}
		return initialURL;
	}

    /**
     * Add an additional parameter, specifically: oauth_body_hash
     * @param authRestTemplate
     * @param body
     * @throws NoSuchAlgorithmException
     */
	private void addAdditionalParam(OAuthRestTemplate authRestTemplate, String body) {
        OAuthClientHttpRequestFactory requestFactory = (OAuthClientHttpRequestFactory) authRestTemplate.getRequestFactory();
        Map<String, String> additionalOAuthParameters = new HashMap<>();
        String hash = doXMLBodyHashSHA1(body);
        additionalOAuthParameters.put("oauth_body_hash", hash);
        requestFactory.setAdditionalOAuthParameters(additionalOAuthParameters);
        LOG.debug("oauth_body_hash:: "+hash);
	}

    /**
     * As per LTI v 1.1.1 specification the request's XML body has to be hashed with SHA-1;
     * The oauth_body_hash [OBH, 11] is computed using a SHA-1 hash of the body contents and added to the Authorization header.
     * @param body
     * @return
     * @throws NoSuchAlgorithmException
     */
	private String doXMLBodyHashSHA1(String body) {
		String algorithmCode = "SHA-1";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algorithmCode);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Failed to hash XML body", e);
		}
		md.update(body.getBytes());
		byte[] output = Base64.encodeBase64(md.digest());
        String hash = new String(output);
		return hash;
	}

}
