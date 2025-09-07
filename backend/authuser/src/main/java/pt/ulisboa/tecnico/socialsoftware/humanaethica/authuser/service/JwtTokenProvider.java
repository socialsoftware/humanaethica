package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.security.RSAUtil;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.common.security.UserInfo;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.common.exceptions.ErrorMessage.AUTHUSER_NOT_FOUND;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private static PublicKey publicKey;

    private static PrivateKey privateKey;

    private static final String PRIVATE_KEY_FILENAME = "private_key.der";

    public JwtTokenProvider() {
    }

    static String generateToken(AuthUser authUser) {
        if (privateKey == null) {
            try {
                InputStream resource = new ClassPathResource(PRIVATE_KEY_FILENAME).getInputStream();
                privateKey = RSAUtil.getPrivateKey(resource);
            } catch (Exception e) {
                logger.info("Failed reading key");
                logger.info(e.getMessage());
            }
        }

        Claims claims = Jwts.claims().setSubject(String.valueOf(authUser.getId()));
        claims.put("role", authUser.getRole());
        claims.put("userId", authUser.getUserId());
        claims.put("username", authUser.getUsername());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000 * 60 * 60 * 24);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(privateKey)
                .compact();
    }
}