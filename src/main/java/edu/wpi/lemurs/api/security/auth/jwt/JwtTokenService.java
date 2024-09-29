/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.jwt;

import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/** A {@link JwtTokenService} is a service for creating and validating jwt tokens. */
@Service
public class JwtTokenService {

  private static final long EXPIRATION_TIME_IN_MILLISECONDS = (long) 60 * 60 * 1000;
  private static final String JWT_SIGNATURE = System.getenv("LEMURS_SIGNATURE");

  /**
   * Generates a jwt token for an authenticated {@link User}.
   *
   * @param authentication The {@link Authentication} for a {@link User}.
   * @return The token.
   * @throws UnauthenticatedException Thrown if no {@link User} is authenticated.
   */
  public String generateToken(Authentication authentication) throws UnauthenticatedException {

    User user;
    Object principal = authentication.getPrincipal();
    if (User.class.isAssignableFrom(principal.getClass())) {
      user = (User) principal;
    } /* else if (UserPrincipal.class.isAssignableFrom(principal.getClass())) {
        UserPrincipal userPrincipal = (UserPrincipal) principal;
        user = userPrincipal.getUser();
      } */ else {
      throw new UnauthenticatedException();
    }
    Integer id = user.getId();
    Date currentDate = new Date();
    Date expirationDate = new Date(currentDate.getTime() + EXPIRATION_TIME_IN_MILLISECONDS);

    return Jwts.builder()
        .subject(id.toString())
        .issuedAt(currentDate)
        .expiration(expirationDate)
        .signWith(key())
        .compact();
  }

  /**
   * Returns the secret application key.
   *
   * @return The secret application key.
   */
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SIGNATURE));
  }

  /**
   * Validates a jwt token.
   *
   * @param token The jwt token.
   * @throws MalformedJwtException Thrown if the token is malformed.
   * @throws SecurityException Thrown if decryption failed.
   * @throws ExpiredJwtException Thrown if the token is expired.
   * @throws IllegalArgumentException Thrown if the token is null or whitespace.
   */
  public void validateToken(String token)
      throws MalformedJwtException,
          SecurityException,
          ExpiredJwtException,
          IllegalArgumentException {
    Jwts.parser().verifyWith((SecretKey) key()).build().parse(token);
  }

  /**
   * Gets the {@link Perosn} id within a jwt token.
   *
   * @param token The jwt token.
   * @return The {@link Person} id within the token.
   */
  public Integer getId(String token) {

    return Integer.parseInt(
        Jwts.parser()
            .verifyWith((SecretKey) key())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject());
  }
}
