package util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
//    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final String SECRET_KEY = "6v3sY8T+KpX1oQkFz2YfJw4vNrbh7+X2bWjNQ0Z9+Yk=";


    public static String generateToken(String username) {
        long expirationMillis = 1000 * 60 * 60; // 1 hora
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
