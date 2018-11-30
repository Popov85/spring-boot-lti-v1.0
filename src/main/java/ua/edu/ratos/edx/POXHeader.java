package ua.edu.ratos.edx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class POXHeader {
	
	@JacksonXmlProperty(localName = "imsx_POXHeaderInfo")
	private PoXHeaderInfo info;

	public PoXHeaderInfo getInfo() {
		return info;
	}

	public void setInfo(PoXHeaderInfo info) {
		this.info = info;
	}
}
