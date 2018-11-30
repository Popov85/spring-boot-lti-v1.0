package ua.edu.ratos.edx.web;

import java.net.URI;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import org.imsglobal.lti.launch.LtiOauthVerifier;
import org.imsglobal.lti.launch.LtiVerificationResult;
import org.imsglobal.lti.launch.LtiVerifier;
import org.imsglobal.pox.IMSPOXRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth.common.signature.HMAC_SHA1SignatureMethod;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.security.oauth.provider.filter.CoreOAuthProviderSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;
import net.oauth.signature.OAuthSignatureMethod;
import ua.edu.ratos.edx.GradedResponse;
import ua.edu.ratos.edx.POXBody;
import ua.edu.ratos.edx.POXHeader;
import ua.edu.ratos.edx.PoXHeaderInfo;
import ua.edu.ratos.edx.ReplaceResultRequest;
import ua.edu.ratos.edx.Result;
import ua.edu.ratos.edx.ResultRecord;
import ua.edu.ratos.edx.ResultScore;
import ua.edu.ratos.edx.SourcedGUID;

@Controller
public class LTIController {
	
	private static final String ID = "edx";
	
	private static final String RESULT = "0.6";
	
	private static final String CLIENT_KEY = "ratos_client_key";
	
	private static final String CLIENT_SECRET = "ratos_client_secret";
	
	private static final String LIS_RESULT_SOURCEID 
	
	= "course-v1%3Aedx-ratos-integration-initiative%2B123456%2B2018_T2:localhost-de03e087e09d4629ab61ee44cea69b43:68b10fe547b3045eb73fe49e2901cb3c";
	
	private static final String EDX_COURSE_POST_LINK 
	
	= "http://localhost/courses/course-v1:edx-ratos-integration-initiative+123456+2018_T2/xblock/block-v1:edx-ratos-integration-initiative+123456+2018_T2+type@lti+block@de03e087e09d4629ab61ee44cea69b43/handler_noauth/grade_handler";
	
	private static final String EDX_COURSE_POST_LINK_MOCK 
	
	= "http://localhost:8090/ratos/receive";
	
	/*@Autowired
	private CoreOAuthProviderSupport providerSupport;*/
	

	
	@PostMapping("/ratos/start")
	@ResponseBody
	public String startPost(HttpServletRequest request) throws Exception {
		Map params = request.getParameterMap();
	    Iterator i = params.keySet().iterator();
	    while ( i.hasNext() ) {
	        String key = (String) i.next();
	        String value = ((String[]) params.get( key ))[ 0 ];
	        System.out.println("key :: "+key);
			System.out.println("value :: "+value);
	      }
	    
	    LtiVerifier ltiVerifier = new LtiOauthVerifier();
	    String key = request.getParameter("oauth_consumer_key");
	    String secret = "ratos_client_secret";// retrieve corresponding secret for key from db
	    LtiVerificationResult ltiResult = ltiVerifier.verify(request, secret);
	    System.out.println("success:: "+ltiResult.getSuccess());
	    System.out.println("error:: "+ltiResult.getError());
	    System.out.println("message:: "+ltiResult.getMessage());
	    
		return "Start button";
	}
	
	@PostMapping("/ratos/start-2")
	@ResponseBody
	public String startPost2(HttpServletRequest request) throws Exception {
		Map params = request.getParameterMap();
	    Iterator i = params.keySet().iterator();
	    while ( i.hasNext() ) {
	        String key = (String) i.next();
	        String value = ((String[]) params.get( key ))[ 0 ];
	        System.out.println("key :: "+key);
			System.out.println("value :: "+value);
	      }
	   
	    String key = request.getParameter("oauth_consumer_key");
	    String signature = request.getParameter("oauth_signature");
	    
	    String secret = "ratos_client_secret";
		
	    // Spring processing
		CoreOAuthProviderSupport providerSupport = new CoreOAuthProviderSupport();
		String signatureBaseString = providerSupport.getSignatureBaseString(request);
		System.out.println("signatureBaseString (Spring) :: "+signatureBaseString);
		
		// lib processing
        OAuthMessage message = OAuthServlet.getMessage(request, OAuthServlet.getRequestURL(request));
		String baseString = OAuthSignatureMethod.getBaseString(message);
		System.out.println("signatureBaseString (lib) :: "+baseString);
		
		System.out.println("is equal :: "+(signatureBaseString.equals(baseString)));

	
		SecretKey secretKey = new SecretKeySpec(secret.getBytes(), "AES"); 
		HMAC_SHA1SignatureMethod signatureMethod = new HMAC_SHA1SignatureMethod(secretKey);
		signatureMethod.verify(signatureBaseString, signature);
		System.out.println("Success verification");
		return "Start button-2";
	}
	
	@CrossOrigin({"http://localhost:18010", "http://localhost"})
	@PostMapping("/oauth/ratos/start-3")
	@ResponseBody
	public String startPost3(HttpServletRequest request) throws Exception {
		// here should work
		return "Start button";
	}
	
	
	@GetMapping("/ratos/post-score")
	@ResponseBody
	public String postScore() throws Exception {
		IMSPOXRequest.sendReplaceResult(EDX_COURSE_POST_LINK, CLIENT_KEY, CLIENT_SECRET, LIS_RESULT_SOURCEID, RESULT);
		return "OK";
	}
	
	@GetMapping("/ratos/post-score-2")
	@ResponseBody
	public String postScore2() throws Exception {
		BaseProtectedResourceDetails resourceDetails = new BaseProtectedResourceDetails();
		resourceDetails.setConsumerKey(CLIENT_KEY);
		resourceDetails.setSharedSecret(new SharedConsumerSecretImpl(CLIENT_SECRET));

		OAuthRestTemplate authRestTemplate = new OAuthRestTemplate(resourceDetails);
		
		GradedResponse gradedResponse = new GradedResponse();
		POXHeader poxHeader = new POXHeader();
		PoXHeaderInfo poXHeaderInfo = new PoXHeaderInfo();
		poXHeaderInfo.setImsx_messageIdentifier(String.valueOf(new Date().getTime()));
		poxHeader.setInfo(poXHeaderInfo);
		gradedResponse.setPoxHeader(poxHeader);
		POXBody poxBody = new POXBody();
		ReplaceResultRequest replaceResultRequest = new ReplaceResultRequest();
		ResultRecord resultRecord = new ResultRecord();
		Result r = new Result();
		
		ResultScore resultScore = new ResultScore();
		
		SourcedGUID sourcedGUID = new SourcedGUID();
		sourcedGUID.setSourcedId(LIS_RESULT_SOURCEID);
		resultRecord.setSource(sourcedGUID);
		
		resultScore.setTextString(RESULT);
		r.setResultScore(resultScore);
		resultRecord.setResult(r);
		
		replaceResultRequest.setResultRecord(resultRecord);
		
		poxBody.setReplaceResultRequest(replaceResultRequest);
		gradedResponse.setPoxBody(poxBody);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
		
	    String body = xmlMapper.writeValueAsString(gradedResponse);
		
		HttpEntity<String> request = new HttpEntity<String>(body, headers);


		ResponseEntity<String> result = authRestTemplate.postForEntity(new URI(EDX_COURSE_POST_LINK),request, String.class);
		System.out.println("RESULT ::"+result.getStatusCode());
		

		return "OK";
	}
	
	@PostMapping("/ratos/receive")
	@ResponseBody
	public String receiveScore(@RequestBody String body, HttpServletRequest request) throws Exception {
		System.out.println("body :: " + body);
		System.out.println("headers ::");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		Enumeration<String> headerNames = httpRequest.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String nextElement = headerNames.nextElement();
				System.out.println("Header :: " + nextElement + " ::"
						+ httpRequest.getHeader(nextElement));
			}
		}

		return "OK";
	}

}
