package ua.edu.ratos.edx.web.domain.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class IMSXStatusInfo {
	
	@JacksonXmlProperty(localName = "imsx_codeMajor")
	private String imsxCodeMajor;
	
	@JacksonXmlProperty(localName = "imsx_severity")
	private String imsxSeverity;
	
	@JacksonXmlProperty(localName = "imsx_description")
	private String imsxDescription;
	
	@JacksonXmlProperty(localName = "imsx_messageRefIdentifier")
	private String imsxMessageRefIdentifier;
	
	@JacksonXmlProperty(localName = "imsx_operationRefIdentifier")
	private String imsxOperationRefIdentifier;

	public String getImsxCodeMajor() {
		return imsxCodeMajor;
	}

	public void setImsxCodeMajor(String imsxCodeMajor) {
		this.imsxCodeMajor = imsxCodeMajor;
	}

	public String getImsxSeverity() {
		return imsxSeverity;
	}

	public void setImsxSeverity(String imsxSeverity) {
		this.imsxSeverity = imsxSeverity;
	}

	public String getImsxDescription() {
		return imsxDescription;
	}

	public void setImsxDescription(String imsxDescription) {
		this.imsxDescription = imsxDescription;
	}

	public String getImsxMessageRefIdentifier() {
		return imsxMessageRefIdentifier;
	}

	public void setImsxMessageRefIdentifier(String imsxMessageRefIdentifier) {
		this.imsxMessageRefIdentifier = imsxMessageRefIdentifier;
	}

	public String getImsxOperationRefIdentifier() {
		return imsxOperationRefIdentifier;
	}

	public void setImsxOperationRefIdentifier(String imsxOperationRefIdentifier) {
		this.imsxOperationRefIdentifier = imsxOperationRefIdentifier;
	}

}
