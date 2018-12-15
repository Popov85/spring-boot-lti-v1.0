package ua.edu.ratos.edx.web.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class POXHeader {
	
	@JacksonXmlProperty(localName = "imsx_POXHeaderInfo")
	private POXHeaderInfo info;

	public POXHeaderInfo getInfo() {
		return info;
	}

	public void setInfo(POXHeaderInfo info) {
		this.info = info;
	}
}
