package pt.ulisboa.tecnico.socialsoftware.humanaethica.api.config;

import org.springframework.core.io.ClassPathResource;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;


public class ApiGatewaySecurityUtils {

    private static PublicKey publicKey;

    private static final String PUBLIC_KEY_FILENAME = "public_key.der";

    public static PublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                InputStream resource = new ClassPathResource(PUBLIC_KEY_FILENAME).getInputStream();
                publicKey = getPublicKey(resource);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load public key from classpath resource: " + PUBLIC_KEY_FILENAME, e);
            }
        }
        return publicKey;
    }

    public static PublicKey getPublicKey(InputStream inputStream) throws Exception {

        byte[] keyBytes = inputStream.readAllBytes();

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static boolean isTokenExpired(String token) {
        Claims tokenClaims = Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getBody();
        return tokenClaims.getExpiration().before(new Date());
    }
}
