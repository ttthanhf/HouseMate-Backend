/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.utils;

import housemate.entities.JwtPayload;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author ThanhF
 */
@Component
public class JwtUtil {

    private final String secretKey;
    private final long jwtExpiration;

    public JwtUtil(
            @Value("${application.security.jwt.secret-key}") String secretKey,
            @Value("${application.security.jwt.expiration}") long jwtExpiration
    ) {
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    public Map<String, Object> extractPayload(String token) {
        Map<String, Object> payloadMap = extractClaim(token, claims -> claims.get("payload", Map.class));
        return payloadMap;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Map<String, Object> extraClaims) {
        return buildToken(extraClaims, jwtExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, JwtPayload jwtPayload) {
        final Map<String, Object> payload = extractPayload(token);
        return isPayloadEqual(payload, jwtPayload.toMap()) && !isTokenExpired(token);
    }

    private boolean isPayloadEqual(Map<String, Object> payload1, Map<String, Object> payload2) {
        if (payload1.size() != payload2.size()) {
            return false;
        }

        for (Map.Entry<String, Object> entry : payload1.entrySet()) {
            String key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = payload2.get(key);

            if (value1 == null && value2 == null) {
                continue;
            }

            if (value1 == null || value2 == null || !value1.equals(value2)) {
                return false;
            }
        }

        return true;
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
