package toy.mail;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JwtUtils {

    private static String key = "this-is-a-test-256-bit-secret-key";
    private static SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes());

    public static String createJwt(String email) {
        JwtBuilder builder = Jwts.builder();

        return builder
                .setSubject(email)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}