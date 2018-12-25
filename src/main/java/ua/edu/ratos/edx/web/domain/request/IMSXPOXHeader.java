package ua.edu.ratos.edx.web.domain.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class IMSXPOXHeader {
	
	@JacksonXmlProperty(localName = "imsx_POXRequestHeaderInfo")
	private IMSXPOXRequestHeaderInfo imsxPOXRequestHeaderInfo;
	

	public IMSXPOXRequestHeaderInfo getImsxPOXRequestHeaderInfo() {
		return imsxPOXRequestHeaderInfo;
	}

	public IMSXPOXHeader setImsxPOXRequestHeaderInfo(IMSXPOXRequestHeaderInfo imsxPOXRequestHeaderInfo) {
		this.imsxPOXRequestHeaderInfo = imsxPOXRequestHeaderInfo;
		return this;
	}
}
