package ua.edu.ratos.edx.web.domain.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


@JacksonXmlRootElement(localName = "imsx_POXEnvelopeResponse")
public class IMSXPOXEnvelopeResponse {
	
	@JacksonXmlProperty(isAttribute = true)
	private String xmlns;
	
	private IMSXPOXResponseHeader imsxPOXHeader;
	
	private IMSXPOXResponseBody imsxPOXBody;

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}
	
	public String getXmlns() {
		return xmlns;
	}

	public IMSXPOXResponseHeader getIMSXPOXHeader() {
		return imsxPOXHeader;
	}

	@JacksonXmlProperty(localName = "imsx_POXHeader")
	public IMSXPOXEnvelopeResponse setIMSXPOXHeader(IMSXPOXResponseHeader IMSXPOXHeader) {
		this.imsxPOXHeader = IMSXPOXHeader;
		return this;
	}

	public IMSXPOXResponseBody getIMSXPOXBody() {
		return imsxPOXBody;
	}

	@JacksonXmlProperty(localName = "imsx_POXBody")
	public IMSXPOXEnvelopeResponse setIMSXPOXBody(IMSXPOXResponseBody IMSXPOXBody) {
		this.imsxPOXBody = IMSXPOXBody;
		return this;
	}

}
