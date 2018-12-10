package ua.edu.ratos.edx.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * These fields (lis_person_name_given and lis_person_name_family) contain information about the user account that is performing this launch.
 * The names of these data items are taken from LIS [LIS, 11].
 * The precise meaning of the content in these fields is defined by LIS.
 * These parameters are recommended unless they are suppressed because of privacy settings.
 * @see <a href="http://www.imsglobal.org/specs/ltiv1p1p1/implementation-guide">LTI v 1.1.1</a>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ToolConsumerUserLTI1p0 {
    /**
     * As per LTI v 1.1.1 specification the request parameter is called: "lis_person_name_given"
     */
    private final String name;
    /**
     * As per LTI v 1.1.1 specification the request parameter is called: "lis_person_name_family"
     */
    private final String surname;
}
