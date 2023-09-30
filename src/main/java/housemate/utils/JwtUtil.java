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
import org.springframework.stereotype.Component;

/**
 *
 * @author ThanhF
 */
@Component
public class JwtUtil {

//    @Value("${application.security.jwt.secret-key}")
    private final String secretKey = "asdhhashdsjkhdashkadsheffeajnagkrjnagnjrgknjagrknjrgkjngragrjngrkjngrkjnefajneef";

//    @Value("${application.security.jwt.expiration}")
    private final long jwtExpiration = 36000000;

    public String extractEmail(String token) {
        Map<String, Object> payloadMap = extractClaim(token, claims -> claims.get("payload", Map.class));
        return (String) payloadMap.get("email");
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
        final String email = extractEmail(token);
        return (email.equals(jwtPayload.getEmail())) && !isTokenExpired(token);
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
