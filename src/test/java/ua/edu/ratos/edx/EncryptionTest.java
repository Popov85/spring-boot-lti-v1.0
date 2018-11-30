package ua.edu.ratos.edx;

import static org.junit.Assert.*;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.imsglobal.lti.launch.LtiError;
import org.imsglobal.lti.launch.LtiVerificationResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.security.oauth.common.signature.HMAC_SHA1SignatureMethod;
import org.springframework.security.oauth.common.signature.PlainTextSignatureMethod;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.OAuthServlet;

@RunWith(JUnit4.class)
public class EncryptionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	public void test() {
		
	    String secret = "ratos_client_secret";
	    
	    String signature = "Mt4gim9T6L1WC6kWVikqvcNEydY=";
	    
	    String baseString = "POST&http%3A%2F%2Flocalhost%3A8090%2Fratos%2Fstart-2&context_id%3Dcourse-v1%253Aedx-ratos-integration-initiative%252B123456%252B2018_T2%26launch_presentation_return_url%3D%26lis_outcome_service_url%3D%252Fpreview%252Fxblock%252Fblock-v1%253Aedx-ratos-integration-initiative%252B123456%252B2018_T2%252Btype%2540lti%252Bblock%2540de03e087e09d4629ab61ee44cea69b43%252Fhandler%252Fgrade_handler%26lis_result_sourcedid%3Dcourse-v1%25253Aedx-ratos-integration-initiative%25252B123456%25252B2018_T2%253A-de03e087e09d4629ab61ee44cea69b43%253Astudent%26lti_message_type%3Dbasic-lti-launch-request%26lti_version%3DLTI-1p0%26oauth_callback%3Dabout%253Ablank%26oauth_consumer_key%3Dratos_client_key%26oauth_nonce%3D111879859424059365771543479509%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1543479509%26oauth_version%3D1.0%26resource_link_id%3D-de03e087e09d4629ab61ee44cea69b43%26roles%3DInstructor%26user_id%3Dstudent";
	    
	    SecretKey secretKey = new SecretKeySpec(secret.getBytes(), "AES"); 
		HMAC_SHA1SignatureMethod signatureMethod = new HMAC_SHA1SignatureMethod(secretKey);
		
		signatureMethod.verify(baseString, signature);
		System.out.println("Success verification");
	}
	
	@Test
	@Ignore
	public void testLib() throws Exception {
		String secret = "ratos_client_secret";
		
		OAuthMessage oam = new OAuthMessage("POST", "", Collections.emptyList()); //OAuthServlet.getMessage(request, OAuthServlet.getRequestURL(request));
		String oauth_consumer_key = null;

		oauth_consumer_key = oam.getConsumerKey();

		OAuthValidator oav = new SimpleOAuthValidator();
		OAuthConsumer cons = new OAuthConsumer(null, oauth_consumer_key, secret, null);
		OAuthAccessor acc = new OAuthAccessor(cons);

		oav.validateMessage(oam, acc);
	}

}
