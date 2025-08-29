package com.example.digigoods.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for JWT token operations.
 */
@Service
public class JwtService {

  @Value("${jwt.secret:mySecretKey}")
  private String secret;

  @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
  private Long expiration;

  /**
   * Generate JWT token for a user.
   *
   * @param userId the user ID
   * @param username the username
   * @return JWT token
   */
  public String generateToken(Long userId, String username) {
    return Jwts.builder()
        .subject(username)
        .claim("userId", userId)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * Extract username from JWT token.
   *
   * @param token the JWT token
   * @return username
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extract user ID from JWT token.
   *
   * @param token the JWT token
   * @return user ID
   */
  public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
  }

  /**
   * Check if JWT token is expired.
   *
   * @param token the JWT token
   * @return true if expired, false otherwise
   */
  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Validate JWT token.
   *
   * @param token the JWT token
   * @param username the username to validate against
   * @return true if valid, false otherwise
   */
  public boolean validateToken(String token, String username) {
    final String extractedUsername = extractUsername(token);
    return (extractedUsername.equals(username) && !isTokenExpired(token));
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }
}
