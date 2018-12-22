package ua.edu.ratos.edx.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.imsglobal.pox.IMSPOXRequest;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.http.HttpParameters;

public class MYIMSPOXRequest extends IMSPOXRequest {
	
	static final String resultDataText = "<resultData><text>%s</text></resultData>";
	
	static final String resultDataUrl = "<resultData><url>%s</url></resultData>";

	public MYIMSPOXRequest(HttpServletRequest request) {
		super(request);
	}
	
	public static void sendReplaceResult(String url, String key, String secret, String sourcedid, String score) throws IOException, OAuthException, GeneralSecurityException {
		sendReplaceResult(url, key, secret, sourcedid, score, null);
	}

	public static void sendReplaceResult(String url, String key, String secret, String sourcedid, String score, String resultData) throws IOException, OAuthException, GeneralSecurityException {
		sendReplaceResult(url, key, secret, sourcedid, score, resultData, false);
	}

	public static void sendReplaceResult(String url, String key, String secret, String sourcedid, String score, String resultData, Boolean isUrl) throws IOException, OAuthException, GeneralSecurityException {
		HttpPost request = buildReplaceResult(url, key, secret, sourcedid, score, resultData, isUrl);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() >= 400) {
			throw new HttpResponseException(response.getStatusLine().getStatusCode(),
					response.getStatusLine().getReasonPhrase());
		}
	}
	
	public static HttpPost buildReplaceResult(String url, String key, String secret, String sourcedid, String score, String resultData, Boolean isUrl) throws IOException, OAuthException, GeneralSecurityException {
		return buildReplaceResult(url, key, secret, sourcedid, score, resultData, isUrl, null);
	}

	public static HttpPost buildReplaceResult(String url, String key, String secret, String sourcedid,
		String score, String resultData, Boolean isUrl, String messageId) throws IOException, OAuthException, GeneralSecurityException
	{
		String dataXml = "";
		if (resultData != null) {
			String format = isUrl ? resultDataUrl : resultDataText;
			dataXml = String.format(format, StringEscapeUtils.escapeXml(resultData));
		}

		String messageIdentifier = StringUtils.isBlank(messageId) ? String.valueOf(new Date().getTime()) : messageId;
		String xml = String.format(ReplaceResultMessageTemplate,
			StringEscapeUtils.escapeXml(messageIdentifier),
			StringEscapeUtils.escapeXml(sourcedid),
			StringEscapeUtils.escapeXml(score),
			dataXml);
		
		System.out.println("xml body :: "+xml);

		HttpParameters parameters = new HttpParameters();
		String hash = getBodyHash(xml);
		parameters.put("oauth_body_hash", URLEncoder.encode(hash, "UTF-8"));

		CommonsHttpOAuthConsumer signer = new CommonsHttpOAuthConsumer(key, secret);
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "application/xml");
		request.setEntity(new StringEntity(xml, "UTF-8"));
		signer.setAdditionalParameters(parameters);
		signer.sign(request);
		return request;
	}

}
