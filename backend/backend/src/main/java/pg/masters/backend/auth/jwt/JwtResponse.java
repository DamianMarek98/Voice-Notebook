package pg.masters.backend.auth.jwt;

import java.io.Serializable;

public record JwtResponse(String jwtToken, String expiresIn) implements Serializable {
}
