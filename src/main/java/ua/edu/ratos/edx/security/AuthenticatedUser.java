package ua.edu.ratos.edx.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class AuthenticatedUser extends User {
 
	private static final long serialVersionUID = 1L;
	
	private Long userId;

    public AuthenticatedUser(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
