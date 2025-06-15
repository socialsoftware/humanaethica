package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.dto.AuthUserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.repository.AuthUserRepository;


import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.auth.Type;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.domain.User.State;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.user.repository.UserRepository;

@Service
public class DemoService {
    @Autowired
    AuthUserRepository authUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthUserDto getDemoAdmin() {
        AuthUser authUser = authUserRepository.findAuthUserByUsername("ars").orElseGet(() -> {

            Integer userId = userService.createAdmin("ars", "ars", "ars_admin@mail.com",  State.ACTIVE);
            AuthUser authuser = AuthUser.createAuthUser(userId,"ars", "ars_admin@mail.com", Type.DEMO, Role.ADMIN);
            authuser.setPassword(passwordEncoder.encode("ars"));
            authuser = authUserRepository.save(authuser);
            return authuser;
        });

        return new AuthUserDto(authUser);
    }
}
