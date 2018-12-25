package ua.edu.ratos.edx.web.domain.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public class IMSXPOXResponseHeader {
	
	@JacksonXmlProperty(localName = "imsx_POXResponseHeaderInfo")
	private IMSXPOXResponseHeaderInfo imsxPOXResponseHeaderInfo;

	public IMSXPOXResponseHeaderInfo getImsxPOXResponseHeaderInfo() {
		return imsxPOXResponseHeaderInfo;
	}

	public void setImsxPOXResponseHeaderInfo(IMSXPOXResponseHeaderInfo imsxPOXResponseHeaderInfo) {
		this.imsxPOXResponseHeaderInfo = imsxPOXResponseHeaderInfo;
	}
	
	
}
