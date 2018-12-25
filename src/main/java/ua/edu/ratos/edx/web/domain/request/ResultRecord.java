package ua.edu.ratos.edx.web.domain.request;

public class ResultRecord {

	private SourcedGUID sourcedGUID;
	
	private Result result;

	public SourcedGUID getSourcedGUID() {
		return sourcedGUID;
	}

	public ResultRecord setSourcedGUID(SourcedGUID sourcedGUID) {
		this.sourcedGUID = sourcedGUID;
		return this;
	}

	public Result getResult() {
		return result;
	}

	public ResultRecord setResult(Result result) {
		this.result = result;
		return this;
	}
}
