package ua.edu.ratos.edx.web.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "imsx_POXEnvelopeRequest")
public class IMSXPOXEnvelopeRequest {
	
	@JacksonXmlProperty(isAttribute = true)
	private final String xmlns = "http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0";
	
	@JacksonXmlProperty(localName = "imsx_POXHeader")
	private IMSXPOXHeader IMSXPOXHeader;
	
	@JacksonXmlProperty(localName = "imsx_POXBody")
	private IMSXPOXBody IMSXPOXBody;

	public String getXmlns() {
		return xmlns;
	}

	public ua.edu.ratos.edx.web.domain.IMSXPOXHeader getIMSXPOXHeader() {
		return IMSXPOXHeader;
	}

	public IMSXPOXEnvelopeRequest setIMSXPOXHeader(ua.edu.ratos.edx.web.domain.IMSXPOXHeader IMSXPOXHeader) {
		this.IMSXPOXHeader = IMSXPOXHeader;
		return this;
	}

	public ua.edu.ratos.edx.web.domain.IMSXPOXBody getIMSXPOXBody() {
		return IMSXPOXBody;
	}

	public IMSXPOXEnvelopeRequest setIMSXPOXBody(ua.edu.ratos.edx.web.domain.IMSXPOXBody IMSXPOXBody) {
		this.IMSXPOXBody = IMSXPOXBody;
		return this;
	}
}
