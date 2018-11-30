package ua.edu.ratos.edx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "imsx_POXEnvelopeRequest")
public class GradedResponse {
	
	@JacksonXmlProperty(isAttribute = true)
	private String xmlns = "http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0";
	
	@JacksonXmlProperty(localName = "imsx_POXHeader")
	private POXHeader poxHeader;
	
	@JacksonXmlProperty(localName = "imsx_POXBody")
	private POXBody poxBody;

	
	public POXHeader getPoxHeader() {
		return poxHeader;
	}

	public void setPoxHeader(POXHeader poxHeader) {
		this.poxHeader = poxHeader;
	}

	public POXBody getPoxBody() {
		return poxBody;
	}

	public void setPoxBody(POXBody poxBody) {
		this.poxBody = poxBody;
	}
	
	

	
	
}
