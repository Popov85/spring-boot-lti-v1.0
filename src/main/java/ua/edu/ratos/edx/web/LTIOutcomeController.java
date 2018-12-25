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
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/student")
public class LTIOutcomeController {
	
	private static final Log LOG = LogFactory.getLog(LTIOutcomeController.class);

    @Autowired
    private LTIOutcomeService ltiOutcomeService;

    private static final String SCORE = "0.75";


    private static final String CLIENT_SECRET = "ratos_client_secret";


    //private static final String EDX_COURSE_POST_LINK_MOCK = "http://localhost:8090/ratos/receive";


    @GetMapping("/post-score-ims")
    @ResponseBody
    public String postScoreIms(Authentication authentication) throws Exception {
    	LTIUserConsumerCredentials principal = (LTIUserConsumerCredentials)authentication.getPrincipal();
    	Optional<LTIOutcomeParams> outcome = principal.getOutcome();
    	if (!outcome.isPresent()) {
    		throw new RuntimeException("Cannot post the score, no outcome info!");
    	}
    	
    	// edX sends https callback URL even if the actual protocol is http
    	String outcomeURL = outcome.get().getOutcomeURL();
    	
        String newOutcomeURL = outcomeURL.replaceAll("https", "http");
    	
    	LOG.debug("Try to post score with IMS :: "+SCORE);

        MYIMSPOXRequest.sendReplaceResult(newOutcomeURL, principal.getConsumerKey(), CLIENT_SECRET, outcome.get().getSourcedId(), SCORE);
        
    	LOG.debug("Success IMS :: posted score :: "+SCORE);
        return "OK";
    }
    

    @GetMapping("/post-score-spring")
    @ResponseBody
    public String postScoreSpring(Authentication authentication, HttpServletRequest request) throws Exception {
    	LOG.debug("Authentication :: "+authentication);
    	LOG.debug("Try to post score with Spring :: "+SCORE);
    	
        ltiOutcomeService.sendOutcome(authentication, request.getScheme(), 1L, Double.parseDouble(SCORE));
        
    	LOG.debug("Success posting with Spring :: "+SCORE);
        return "OK";
    }
   
   
}
