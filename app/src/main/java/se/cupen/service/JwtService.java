package se.cupen.service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final String SECRET_KEY;

  public JwtService(@Value("${jwt.key}") String secretKey) {
    this.SECRET_KEY = secretKey;
  }

  public String generateToken(User user, Date expiration) {

    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("email", user.getEmail())
        .claim("name", user.getForename() + " " + user.getSurname())
        .setIssuedAt(new Date())
        .setExpiration(expiration)
        .signWith(getSecretKey(), SignatureAlgorithm.HS256)
        .compact();

  }

  public Key getSecretKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  public String extractOrgId(String token) {
    return extractClaim(token, claims -> claims.get("orgId", String.class));
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claimsResolver.apply(claims);
  }

  public boolean validateToken(String token, String username) {
    return (username.equals(extractUsername(token)) && !isTokenExpired(token));
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }
}
