package ua.edu.ratos.edx.web;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.imsglobal.pox.IMSPOXRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ratos.edx.web.domain.*;

import java.net.URI;
import java.util.Date;

@RestController
public class LTIPostScoreController {

    private static final String RESULT = "0.6";

    private static final String CLIENT_KEY = "ratos_client_key";

    private static final String CLIENT_SECRET = "ratos_client_secret";

    private static final String LIS_RESULT_SOURCEID

            = "course-v1%3Aedx-ratos-integration-initiative%2B123456%2B2018_T2:localhost-de03e087e09d4629ab61ee44cea69b43:68b10fe547b3045eb73fe49e2901cb3c";

    private static final String EDX_COURSE_POST_LINK

            = "http://localhost/courses/course-v1:edx-ratos-integration-initiative+123456+2018_T2/xblock/block-v1:edx-ratos-integration-initiative+123456+2018_T2+type@lti+block@de03e087e09d4629ab61ee44cea69b43/handler_noauth/grade_handler";

    private static final String EDX_COURSE_POST_LINK_MOCK

            = "http://localhost:8090/ratos/receive";


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
        POXHeaderInfo POXHeaderInfo = new POXHeaderInfo();
        POXHeaderInfo.setImsx_messageIdentifier(String.valueOf(new Date().getTime()));
        poxHeader.setInfo(POXHeaderInfo);
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
}
