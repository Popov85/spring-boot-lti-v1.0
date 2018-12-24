package ua.edu.ratos.edx.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ua.edu.ratos.edx.domain.Student;
import ua.edu.ratos.edx.repository.LocalStudentHolder;
import java.util.Optional;

@Component
public class AuthenticatedUserDetails implements UserDetailsService {

    @Autowired
    private LocalStudentHolder localStudentHolder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Student> student = localStudentHolder.getByEmail(email);
        if (student.isPresent()) {
            return new AuthenticatedUser(student.get().getUser().getUserId(),
                    email,
                    String.valueOf(student.get().getUser().getPassword()),
                    AuthorityUtils.createAuthorityList("ROLE_STUDENT"));
        }
        throw new UsernameNotFoundException("DAO authentication failed, no such user e-mail exists in the DB");
    }

}
