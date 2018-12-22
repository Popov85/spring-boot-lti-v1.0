package ua.edu.ratos.edx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.edu.ratos.edx.security.lti.LTIOutcomeParams;
import ua.edu.ratos.edx.security.lti.LTIUserConsumerCredentials;
import ua.edu.ratos.edx.service.LTIOutcomeService;
import ua.edu.ratos.edx.service.MYIMSPOXRequest;
import ua.edu.ratos.edx.service.XMLScoreRequestBodyBuilder;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class LTIOutcomeController {
	
	private static final Log LOG = LogFactory.getLog(LTIOutcomeController.class);

    @Autowired
    private LTIOutcomeService ltiOutcomeService;

    private static final String SCORE = "0.95";

    private static final String CLIENT_KEY = "ratos_client_key";

    private static final String CLIENT_SECRET = "ratos_client_secret";

    private static final String LIS_RESULT_SOURCEID

            = "course-v1%3Aedx-ratos-integration-initiative%2B123456%2B2018_T2:localhost-de03e087e09d4629ab61ee44cea69b43:68b10fe547b3045eb73fe49e2901cb3c";

    private static final String EDX_COURSE_POST_LINK

            = "http://localhost/courses/course-v1:edx-ratos-integration-initiative+123456+2018_T2/xblock/block-v1:edx-ratos-integration-initiative+123456+2018_T2+type@lti+block@de03e087e09d4629ab61ee44cea69b43/handler_noauth/grade_handler";

    private static final String EDX_COURSE_POST_LINK_MOCK = "http://localhost:8090/ratos/receive";


    @GetMapping("/post-score-ims")
    @ResponseBody
    public String postScoreIms(Authentication authentication) throws Exception {
    	LTIUserConsumerCredentials principal = (LTIUserConsumerCredentials)authentication.getPrincipal();
    	Optional<LTIOutcomeParams> outcome = principal.getOutcome();
    	if (!outcome.isPresent()) {
    		throw new RuntimeException("Cannot post the score, no outcome info!");
    	}
    	
    	String outcomeURL = outcome.get().getOutcomeURL();
    	
        String newOutcomeURL = outcomeURL.replaceAll("https", "http");
    	
    	LOG.debug("Try to post score with IMS :: "+SCORE);

        MYIMSPOXRequest.sendReplaceResult(EDX_COURSE_POST_LINK_MOCK, principal.getConsumerKey(), 
        		CLIENT_SECRET, outcome.get().getSourcedId(), SCORE);
    	LOG.debug("Success :: posted score :: "+SCORE);
        return "OK";
    }
    
    

    @GetMapping("/post-score-spring")
    @ResponseBody
    public String postScoreSpring(Authentication authentication) throws Exception {
    	LOG.debug("Authentication :: "+authentication);
    	LOG.debug("Try to post score with Spring :: "+SCORE);
        ltiOutcomeService.sendOutcome(authentication, Double.parseDouble(SCORE));
        return "OK";
    }
    
    
    @Autowired
	private XMLScoreRequestBodyBuilder xmlScoreRequestBodyBuilder;
    
    @GetMapping("/body")
    @ResponseBody
    public String getBody() throws Exception {
    	String messageIdentifier = Long.toString(new Date().getTime());
        String body = xmlScoreRequestBodyBuilder.getEnvelopeRequestBodyAsString("sourcedId", messageIdentifier, SCORE);
        LOG.debug("body :: "+body);
        return body;
    }
   
   
}
