package pg.masters.backend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.masters.backend.auth.exceptions.UsernameNotUniqueException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(String username, String password) throws UsernameNotUniqueException {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameNotUniqueException(username);
        }

        var passwordHash = passwordEncoder.encode(password);
        var user = new User(username, passwordHash);
        userRepository.save(user);
    }

    public User findUserWithNotes(Long userId) {
        return userRepository.findByIdWithNotesFetched(userId).orElse(null);
    }
}
