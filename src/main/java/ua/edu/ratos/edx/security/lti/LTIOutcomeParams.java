package ua.edu.ratos.edx.security.lti;

/**
 * Recommended only if TC want TP to send outcome grades back;
 * The TP records these values as they are sent on launches and can then later make services calls providing the sourcedId as way to pick
 * the particular cell in the TC grade book.
 * @see <a href="http://www.imsglobal.org/specs/ltiv1p1p1/implementation-guide">LTI v 1.1.1</a>
 */
public class LTIOutcomeParams {
    /*
     * As per LTI v 1.1.1 specification the launch request parameter is called: "lis_result_sourcedid".
     */
    private final String sourcedId;
    /**
     * As per LTI v 1.1.1 specification the launch request parameter is called: "lis_outcome_service_url"
     */
    private final String outcomeURL;

    public LTIOutcomeParams(String sourcedId, String outcomeURL) {
        this.sourcedId = sourcedId;
        this.outcomeURL = outcomeURL;
    }

    public String getSourcedId() {
        return sourcedId;
    }

    public String getOutcomeURL() {
        return outcomeURL;
    }

    @Override
    public String toString() {
        return "LTIOutcomeParams{" +
                "sourcedId='" + sourcedId + '\'' +
                ", outcomeURL='" + outcomeURL + '\'' +
                '}';
    }
}
