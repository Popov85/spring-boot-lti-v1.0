package ua.edu.ratos.edx.service;

import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.stereotype.Service;

@Service
public class LTIRetryOutcomeService {
	
	private static final Log LOG = LogFactory.getLog(LTIRetryOutcomeService.class);
	
	/**
	 * Tries multiple times to post the outcome for sure
	 * @param oAuthRestTemplate
	 * @param uri
	 * @param entity
	 */
	@Retryable(maxAttempts = 6, 
			value = org.springframework.web.client.RestClientException.class, backoff = @Backoff(delay = 500, multiplier = 2))
	public void doSend(OAuthRestTemplate oAuthRestTemplate, URI uri, HttpEntity<String> entity, String email, Long schemeId) {
        String result = oAuthRestTemplate.postForObject(uri, entity, String.class);
		LOG.debug("Successfully sent the outcome :: "+result);
	}
	
	
	@Recover
	public void recover(OAuthRestTemplate oAuthRestTemplate, URI uri, HttpEntity<String> entity, String email, Long schemeId) {
		LOG.error("Failed to send the outcome to LMS"
				+ " by user :: "+email
				+ " having taken the scheme :: "+schemeId
				+ " with reason :: probably the LTI service is off");
	}

}
