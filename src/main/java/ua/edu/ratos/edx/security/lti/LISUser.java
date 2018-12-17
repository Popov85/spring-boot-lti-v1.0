package ua.edu.ratos.edx.security.lti;

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
public class LISUser {
    /**
     * As per LTI v 1.1.1 specification the launch request parameter is called: "lis_person_name_given"
     */
    private final String name;
    /**
     * As per LTI v 1.1.1 specification the launch request parameter is called: "lis_person_name_family"
     */
    private final String surname;

    public LISUser(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public String toString() {
        return "LISUser{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
