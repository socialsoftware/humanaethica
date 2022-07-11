package pt.ulisboa.tecnico.socialsoftware.humanaethica.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.dto.AuthUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Admin;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

@Service
public class DemoService {
    @Autowired
    AuthUserRepository authUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUserDto getDemoAdmin() {
        AuthUser authUser = authUserRepository.findAuthUserByUsername("ars").orElseGet(() -> {
            Admin admin = new Admin("ars", "ars", "ars_admin@mail.com", AuthUser.Type.DEMO, State.ACTIVE);
            userRepository.save(admin);
            admin.getAuthUser().setPassword(passwordEncoder.encode("ars"));

            return admin.getAuthUser();
        });

        return new AuthUserDto(authUser);
    }
}
