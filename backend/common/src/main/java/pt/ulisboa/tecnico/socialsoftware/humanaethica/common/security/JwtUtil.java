package pt.ulisboa.tecnico.socialsoftware.humanaethica.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.InputStream;
import java.security.PublicKey;

import org.springframework.stereotype.Component;



@Component
public class JwtUtil {

    private static PublicKey publicKey;

    private static final String PUBLIC_KEY_FILENAME = "public_key.der";

    public static PublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                InputStream resource = new ClassPathResource(PUBLIC_KEY_FILENAME).getInputStream();
                publicKey = RSAUtil.getPublicKey(resource);
            } catch (Exception e) {
                throw new RuntimeException("Unable to load public key from classpath resource: " + PUBLIC_KEY_FILENAME, e);
            }
        }
        return publicKey;
    }

    public static String getToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else if (authHeader != null && authHeader.startsWith("AUTH")) {
            return authHeader.substring(4);
        } else if (authHeader != null) {
            return authHeader;
        }
        return "";
    }

    private static Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getBody();
    }

    Authentication getAuthentication(String token) {
        Claims tokenClaims = getAllClaimsFromToken(token);
        UserInfo userInfo = new UserInfo(tokenClaims.get("userId", Integer.class), tokenClaims.get("username", String.class),tokenClaims.get("role").toString());
        return new UsernamePasswordAuthenticationToken(userInfo, "", userInfo.getAuthorities());
    }
}
