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
import ua.edu.ratos.edx.web.domain.response.IMSXPOXEnvelopeResponse;
import ua.edu.ratos.edx.web.domain.response.IMSXStatusInfo;

@Service
public class LTIRetryOutcomeService {
	
	private static final Log LOG = LogFactory.getLog(LTIRetryOutcomeService.class);
	
	/**
	 * Tries to post the outcome multiple times just to be on the safe side;
	 * Response arrives in XML format, see interpretation below
	 * @param oAuthRestTemplate
	 * @param uri
	 * @param entity
	 * @see <a href="https://www.imsglobal.org/gws/gwsv1p0/imsgws_wsdlBindv1p0.html">	
			IMS General Web Services WSDL Binding Guidelines</a>

	 */
	@Retryable(maxAttempts = 6, 
			value = org.springframework.web.client.RestClientException.class, backoff = @Backoff(delay = 500, multiplier = 2))
	public void doSend(OAuthRestTemplate oAuthRestTemplate, URI uri, HttpEntity<String> entity, String email, Long schemeId) {
		IMSXPOXEnvelopeResponse response = oAuthRestTemplate.postForObject(uri, entity, IMSXPOXEnvelopeResponse.class);
		IMSXStatusInfo imsxStatusInfo = response.getIMSXPOXHeader().getImsxPOXResponseHeaderInfo().getImsxStatusInfo();
		String imsxCodeMajor = imsxStatusInfo.getImsxCodeMajor();
		String imsxDescription = imsxStatusInfo.getImsxDescription();
		if (!imsxCodeMajor.equals("success")) {
			LOG.warn("Outcome was rejected by LMS server by user :: "+ email+" having taken the scheme :: "+ schemeId + " with code :: " + imsxCodeMajor+ " and description :: "+ imsxDescription);
			return;
		}
		LOG.debug("Outcome was successfully accepted by LMS server, by user :: "+ email+ " having taken the scheme :: "+ schemeId+ "with code :: "+ imsxCodeMajor+ " and description :: "+ imsxDescription);
	}
	
	
	@Recover
	public void recover(OAuthRestTemplate oAuthRestTemplate, URI uri, HttpEntity<String> entity, String email, Long schemeId) {
		LOG.error("Failed to send the outcome to LMS by user :: "+email+ " having taken the scheme :: "+schemeId+ " with reason :: probably the LTI service is off");
	}

}
