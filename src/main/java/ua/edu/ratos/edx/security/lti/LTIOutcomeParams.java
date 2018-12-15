package ua.edu.ratos.edx.security.lti;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Recommended only if TC want TP to send outcome grades back;
 * The TP records these values as they are sent on launches and can then later make services calls providing the sourcedId as way to pick
 * the particular cell in the TC grade book.
 * @see <a href="http://www.imsglobal.org/specs/ltiv1p1p1/implementation-guide">LTI v 1.1.1</a>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class LTIOutcomeParams {
    /*
     * As per LTI v 1.1.1 specification the launch request parameter is called: "lis_result_sourcedid".
     */
    private final String sourcedId;
    /**
     * As per LTI v 1.1.1 specification the launch request parameter is called: "lis_outcome_service_url"
     */
    private final String outcomeURL;
}
