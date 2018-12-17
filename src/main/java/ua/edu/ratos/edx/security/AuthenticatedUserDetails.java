package ua.edu.ratos.edx.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@Component
public class AuthenticatedUserDetails implements UserDetailsService {

    private Map<String, String> passwordsHolder = new HashMap<>();

    private Map<String, Long> usersHolder = new HashMap<>();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (usersHolder.get(username)!=null) {
            return new AuthenticatedUser(usersHolder.get(username), username, passwordsHolder.get(username),
                    AuthorityUtils.createAuthorityList("ROLE_STUDENT"));
        }
        throw new UsernameNotFoundException("DAO authentication failed, no such user e-mail exists in the DB");
    }

    @PostConstruct
    public void init() {
        this.usersHolder.put("admin@example.com", 1L);
        this.usersHolder.put("labassist@example.com", 2L);
        this.usersHolder.put("student@example.com", 3L);

        this.passwordsHolder.put("admin@example.com", "{noop}password");
        this.passwordsHolder.put("labassist@example.com", "{noop}password");
        this.passwordsHolder.put("student@example.com", "{noop}password");
    }
}
