package ua.edu.ratos.edx.web.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class IMSXPOXRequestHeaderInfo {

	@JacksonXmlProperty(localName = "imsx_version")
	private final String imsxVersion = "V1.01";

	@JacksonXmlProperty(localName = "imsx_messageIdentifier")
	private String imsxMessageIdentifier;

	public String getImsxVersion() {
		return imsxVersion;
	}

	public String getImsxMessageIdentifier() {
		return imsxMessageIdentifier;
	}

	public IMSXPOXRequestHeaderInfo setImsxMessageIdentifier(String imsxMessageIdentifier) {
		this.imsxMessageIdentifier = imsxMessageIdentifier;
		return this;
	}
}
