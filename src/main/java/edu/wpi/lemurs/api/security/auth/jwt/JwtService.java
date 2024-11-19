/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.jwt;

import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.endpoints.user.UserService;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.security.auth.microsoft.AuthMicrosoftAuthentication;
import edu.wpi.lemurs.api.security.auth.microsoft.AuthMicrosoftService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/** A {@link JwtService} is a service for creating and validating jwt tokens. */
@Service
public class JwtService {

  public static final String TOKEN_PURPOSE_KEY = "purpose";
  public static final String ACCESS_TOKEN = "access";
  public static final String REFRESH_TOKEN = "refresh";

  public static final String TOKEN_LOGIN_METHOD_KEY = "login_method";
  public static final String LOGIN_METHOD_MICROSOFT = "Microsoft";

  /** Access tokens last 1 hour. */
  private static final long ACCESS_TOKEN_EXPIRATION_TIME_IN_MILLISECONDS = (long) 60 * 60 * 1000;

  /** Refresh tokens last 90 days. */
  private static final long REFRESH_TOKEN_EXPIRATION_TIME_IN_MILLISECONDS =
      (long) 90 * 24 * 60 * 60 * 1000;

  private static final String JWT_SIGNATURE = System.getenv("LEMURS_SIGNATURE");

  private AuthMicrosoftService authMicrosoftService;
  private UserService userService;

  @Autowired
  public JwtService(AuthMicrosoftService authMicrosoftService, UserService userService) {
    this.authMicrosoftService = authMicrosoftService;
    this.userService = userService;
  }

  /**
   * Generates an access jwt token for an authenticated {@link User}.
   *
   * @param authentication The {@link Authentication} for a {@link User}.
   * @return The token.
   * @throws UnauthenticatedException Thrown if no {@link User} is authenticated.
   */
  public String generateAccessToken(Authentication authentication) throws UnauthenticatedException {

    User user;
    Object principal = authentication.getPrincipal();
    if (User.class.isAssignableFrom(principal.getClass())) {
      user = (User) principal;
    } else {
      throw new UnauthenticatedException();
    }
    Integer id = user.getId();
    Date currentDate = new Date();
    Date expirationDate =
        new Date(currentDate.getTime() + ACCESS_TOKEN_EXPIRATION_TIME_IN_MILLISECONDS);

    return Jwts.builder()
        .subject(id.toString())
        .claim(TOKEN_PURPOSE_KEY, ACCESS_TOKEN)
        .issuedAt(currentDate)
        .expiration(expirationDate)
        .signWith(key())
        .compact();
  }

  /**
   * Generates a refresh jwt token for an authenticated {@link User}.
   *
   * @param authentication The {@link Authentication} for a {@link User}.
   * @return The token.
   * @throws UnauthenticatedException Thrown if no {@link User} is authenticated.
   */
  public String generateRefreshToken(Authentication authentication)
      throws UnauthenticatedException {

    User user;
    Object principal = authentication.getPrincipal();
    if (User.class.isAssignableFrom(principal.getClass())) {
      user = (User) principal;
    } else {
      throw new UnauthenticatedException();
    }
    Integer id = user.getId();
    Date currentDate = new Date();
    Date expirationDate =
        new Date(currentDate.getTime() + REFRESH_TOKEN_EXPIRATION_TIME_IN_MILLISECONDS);

    String loginMethod = null;

    if (authentication.getClass().isAssignableFrom(AuthMicrosoftAuthentication.class)) {
      loginMethod = LOGIN_METHOD_MICROSOFT;
    }

    return Jwts.builder()
        .subject(id.toString())
        .claim(TOKEN_PURPOSE_KEY, REFRESH_TOKEN)
        .claim(TOKEN_LOGIN_METHOD_KEY, loginMethod)
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
    Jwts.parser()
        .require(TOKEN_PURPOSE_KEY, ACCESS_TOKEN)
        .verifyWith((SecretKey) key())
        .build()
        .parse(token);
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

  public JwtResponse getJWTResponse(Authentication authentication) throws UnauthenticatedException {
    String acccessToken = generateAccessToken(authentication);
    String refreshToken = generateRefreshToken(authentication);

    JwtResponse jwtAuthResponse = new JwtResponse();
    jwtAuthResponse.setAccessToken(acccessToken);
    jwtAuthResponse.setRefreshToken(refreshToken);

    return jwtAuthResponse;
  }

  public JwtResponse useRefreshToken(String refreshToken) throws UnauthenticatedException {
    Claims claims =
        Jwts.parser()
            .require(TOKEN_PURPOSE_KEY, REFRESH_TOKEN)
            .verifyWith((SecretKey) key())
            .build()
            .parseSignedClaims(refreshToken)
            .getPayload();

    String loginMethod = (String) claims.get(TOKEN_LOGIN_METHOD_KEY);
    if (loginMethod == null) {
      throw new UnauthenticatedException();
    }

    Integer userID = Integer.parseInt(claims.getSubject());

    try {
      switch (loginMethod) {
        case LOGIN_METHOD_MICROSOFT:
          Date lastUpdated = authMicrosoftService.getLastUpdated(userID);
          if (lastUpdated.after(claims.getIssuedAt())) {
            throw new UnauthenticatedException();
          }

          return getJWTResponse(
              new AuthMicrosoftAuthentication(loginMethod, userService.getUser(userID)));
        default:
          throw new UnauthenticatedException();
      }
    } catch (EntityDoesNotExistException e) {
      throw new UnauthenticatedException();
    }
  }
}
