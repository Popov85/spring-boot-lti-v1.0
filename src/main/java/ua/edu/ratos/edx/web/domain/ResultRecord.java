package ua.edu.ratos.edx.web.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ResultRecord {
	
	@JacksonXmlProperty(localName = "sourcedGUID")
	private SourcedGUID source;
	
	private Result result;

	public SourcedGUID getSource() {
		return source;
	}

	public void setSource(SourcedGUID source) {
		this.source = source;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
