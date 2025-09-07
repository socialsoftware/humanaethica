package pt.ulisboa.tecnico.socialsoftware.humanaethica.monolithic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.security.JwtTokenFilter;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.security.JwtUtil;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (!activeProfile.equals("prod")) {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .addFilterBefore(new JwtTokenFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        } else {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers( "/users/register/confirm", "/users/registerVolunteer" ,"/images/**", "/resources/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(new JwtTokenFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        }

        return http.build();
    }
}
