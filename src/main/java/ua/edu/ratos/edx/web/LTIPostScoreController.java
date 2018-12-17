package ua.edu.ratos.edx.web;

import org.imsglobal.pox.IMSPOXRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;;
import org.springframework.web.bind.annotation.*;
import ua.edu.ratos.edx.service.LTIOutcomeService;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
public class LTIPostScoreController {

    @Autowired
    private LTIOutcomeService ltiOutcomeService;

    private static final String RESULT = "0.6";

    private static final String CLIENT_KEY = "ratos_client_key";

    private static final String CLIENT_SECRET = "ratos_client_secret";

    private static final String LIS_RESULT_SOURCEID

            = "course-v1%3Aedx-ratos-integration-initiative%2B123456%2B2018_T2:localhost-de03e087e09d4629ab61ee44cea69b43:68b10fe547b3045eb73fe49e2901cb3c";

    private static final String EDX_COURSE_POST_LINK

            = "http://localhost/courses/course-v1:edx-ratos-integration-initiative+123456+2018_T2/xblock/block-v1:edx-ratos-integration-initiative+123456+2018_T2+type@lti+block@de03e087e09d4629ab61ee44cea69b43/handler_noauth/grade_handler";

    private static final String EDX_COURSE_POST_LINK_MOCK

            = "http://localhost:8090/ratos/receive";


    @GetMapping("/ratos/post-score-ims")
    @ResponseBody
    public String postScoreIms() throws Exception {
        IMSPOXRequest.sendReplaceResult(EDX_COURSE_POST_LINK_MOCK, CLIENT_KEY, CLIENT_SECRET, LIS_RESULT_SOURCEID, RESULT);
        return "OK";
    }

    @GetMapping("/ratos/post-score-spring")
    @ResponseBody
    public String postScoreSpring(Authentication authentication) throws Exception {
        System.out.println("Authentication :: "+authentication);
        ltiOutcomeService.sendOutcome(authentication, Double.parseDouble(RESULT));
        return "OK";
    }

    @PostMapping("/ratos/receive")
    @ResponseBody
    public String receiveScore(@RequestBody String body, HttpServletRequest request) throws Exception {
        System.out.println("headers ::");
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String nextElement = headerNames.nextElement();
                System.out.println("Header :: " + nextElement + " ::"
                        + request.getHeader(nextElement));
            }
        }
        System.out.println("body :: " + body);
        return "OK (see stacktrace for received parameters...)";
    }
}
