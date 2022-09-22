package pg.masters.backend.auth;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pg.masters.backend.auth.exceptions.NotAuthenticatedException;

@Service
@NoArgsConstructor
public class AuthService {

    public User getCurrentContextUser() throws NotAuthenticatedException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (checkUnauthenticated(authentication)) {
            throw new NotAuthenticatedException();
        }


       return (User) authentication.getPrincipal();
    }

    private boolean checkUnauthenticated(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null;
    }
}
