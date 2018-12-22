package ua.edu.ratos.edx.service;


import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import ua.edu.ratos.edx.web.domain.IMSXPOXBody;
import ua.edu.ratos.edx.web.domain.IMSXPOXEnvelopeRequest;
import ua.edu.ratos.edx.web.domain.IMSXPOXHeader;
import ua.edu.ratos.edx.web.domain.IMSXPOXRequestHeaderInfo;
import ua.edu.ratos.edx.web.domain.ReplaceResultRequest;
import ua.edu.ratos.edx.web.domain.Result;
import ua.edu.ratos.edx.web.domain.ResultRecord;
import ua.edu.ratos.edx.web.domain.ResultScore;
import ua.edu.ratos.edx.web.domain.SourcedGUID;

@Service
public class XMLScoreRequestBodyBuilder {
	
	private static final Log LOG = LogFactory.getLog(XMLScoreRequestBodyBuilder.class);

	  private XmlMapper xmlMapper;

	    @PostConstruct
	    public void init() {
	        xmlMapper = new XmlMapper();
	        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
	    }
	
	/**
     * Create a Java-object ready for XML serialization according to LTI v1.1.1 specification
     * @param sourcedId a value needed to posting score to LMS as per LTI specification
     * @param messageIdentifier Some value for identifying messages (in our case the number of milliseconds since 1970)
     * @param textScore score between 0-1 gained by a user after learning session completion
     * @return fully populated object ready to be XML-serialized according to LTI specification
	 * @throws Exception 
     * @see <a href="https://www.imsglobal.org/specs/ltiv1p1p1/implementation-guide#toc-3">LTI v 1.1.1</a>
     */
    public String getEnvelopeRequestBodyAsString(String sourcedId, String messageIdentifier, String textScore) throws Exception {
            	
    	IMSXPOXEnvelopeRequest envelopeRequest = 
		new IMSXPOXEnvelopeRequest()
        .setIMSXPOXHeader(new IMSXPOXHeader()
                .setImsxPOXRequestHeaderInfo(new IMSXPOXRequestHeaderInfo()
                        .setImsxMessageIdentifier(messageIdentifier)))
        .setIMSXPOXBody(new IMSXPOXBody()
                .setReplaceResultRequest(new ReplaceResultRequest()
                        .setResultRecord(new ResultRecord()
                                .setSourcedGUID(new SourcedGUID()
                                        .setSourcedId(sourcedId))
                                .setResult(new Result()
                                        .setResultScore(new ResultScore()
                                                .setTextString(textScore))))));
    	
    	String body = xmlMapper.writeValueAsString(envelopeRequest);
    	LOG.debug("body :: "+body);
    	return body;
    }

}
