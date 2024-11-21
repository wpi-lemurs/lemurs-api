/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.jwt;

import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.endpoints.user.UserService;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.security.auth.microsoft.AuthMicrosoftAuthentication;
import edu.wpi.lemurs.api.security.auth.microsoft.AuthMicrosoftService;
import edu.wpi.lemurs.api.services.EnvironmentService;
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

  /** The claim key for the purpose. */
  public static final String PURPOSE_CLAIM_KEY = "purpose";

  /** The access token purpose. */
  public static final String PURPOSE_ACCESS_TOKEN = "access";

  /** The refresh token purpose. */
  public static final String PURPOSE_REFRESH_TOKEN = "refresh";

  /** The claim key for the login method. */
  public static final String LOGIN_METHOD_CLAIM_KEY = "login_method";

  /** The login method for microsoft. */
  public static final String LOGIN_METHOD_MICROSOFT = "Microsoft";

  /** Access tokens expiration length in ms. (1 hour) */
  private static final long ACCESS_TOKEN_EXPIRATION_TIME = (long) 60 * 60 * 1000;

  /** Refresh tokens expiration length in ms. (14 days) */
  private static final long REFRESH_TOKEN_EXPIRATION_TIME = (long) 14 * 24 * 60 * 60 * 1000;

  private EnvironmentService env;
  private AuthMicrosoftService authMicrosoftService;
  private UserService userService;

  /** Autowires a {@link JwtService}. */
  @Autowired
  public JwtService(
      EnvironmentService env, AuthMicrosoftService authMicrosoftService, UserService userService) {
    this.authMicrosoftService = authMicrosoftService;
    this.userService = userService;
    this.env = env;
  }

  /**
   * Throws an exception if the token is not a valid access token.
   *
   * @param token The jwt token.
   * @throws MalformedJwtException Thrown if the token is malformed.
   * @throws SecurityException Thrown if decryption failed.
   * @throws ExpiredJwtException Thrown if the token is expired.
   * @throws IllegalArgumentException Thrown if the token is null or whitespace.
   */
  public void assertValidAccessToken(String token)
      throws MalformedJwtException,
          SecurityException,
          ExpiredJwtException,
          IllegalArgumentException {
    Jwts.parser()
        .require(PURPOSE_CLAIM_KEY, PURPOSE_ACCESS_TOKEN)
        .verifyWith((SecretKey) key())
        .build()
        .parse(token);
  }

  /**
   * Gets the {@link Person} id within a jwt token.
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

  /**
   * Generates the access and refresh tokens based on an authentication.
   *
   * @param authentication The authentication.
   * @return A {@link JwtResponse} with an access and refresh token.
   * @throws UnauthenticatedException Thrown if the authentication is not valid or not for a real
   *     user.
   */
  public JwtResponse getJwtResponse(Authentication authentication) throws UnauthenticatedException {

    if (!authentication.isAuthenticated()) {
      throw new UnauthenticatedException();
    }

    String acccessToken = generateAccessToken(authentication);
    String refreshToken = generateRefreshToken(authentication);

    JwtResponse jwtAuthResponse = new JwtResponse();
    jwtAuthResponse.setAccessToken(acccessToken);
    jwtAuthResponse.setRefreshToken(refreshToken);

    return jwtAuthResponse;
  }

  /**
   * Refreshes access and refresh tokens based on a refresh token.
   *
   * @param refreshToken The refresh token.
   * @return A {@link JwtResponse} with an access and refresh token.
   * @throws UnauthenticatedException Thrown if the refresh token is not valid, or credential have
   *     been updated.
   */
  public JwtResponse refreshJwtResponse(String refreshToken) throws UnauthenticatedException {
    Claims claims =
        Jwts.parser()
            .require(PURPOSE_CLAIM_KEY, PURPOSE_REFRESH_TOKEN)
            .verifyWith((SecretKey) key())
            .build()
            .parseSignedClaims(refreshToken)
            .getPayload();

    String loginMethod = (String) claims.get(LOGIN_METHOD_CLAIM_KEY);
    if (loginMethod == null) {
      throw new UnauthenticatedException();
    }

    Integer userID = Integer.parseInt(claims.getSubject());
    Date issuedAt = claims.getIssuedAt();

    try {
      switch (loginMethod) {
        case LOGIN_METHOD_MICROSOFT:
          authMicrosoftService.assertValidRefreshDate(userID, issuedAt);

          return getJwtResponse(
              new AuthMicrosoftAuthentication(loginMethod, userService.getUser(userID)));
        default:
          throw new UnauthenticatedException();
      }
    } catch (EntityDoesNotExistException e) {
      throw new UnauthenticatedException();
    }
  }

  /**
   * Returns the secret application key.
   *
   * @return The secret application key.
   */
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(env.getJwtSignature()));
  }

  /**
   * Generates an access JWT token for an authenticated {@link User}.
   *
   * @param authentication The {@link Authentication} for a {@link User}.
   * @return The token.
   * @throws UnauthenticatedException Thrown if no {@link User} is authenticated.
   */
  private String generateAccessToken(Authentication authentication)
      throws UnauthenticatedException {

    User user;
    Object principal = authentication.getPrincipal();
    if (User.class.isAssignableFrom(principal.getClass())) {
      user = (User) principal;
    } else {
      throw new UnauthenticatedException();
    }
    Integer id = user.getId();
    Date currentDate = new Date((new Date()).getTime() + 1 * 1000);
    Date expirationDate = new Date(currentDate.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

    return Jwts.builder()
        .subject(id.toString())
        .claim(PURPOSE_CLAIM_KEY, PURPOSE_ACCESS_TOKEN)
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
  private String generateRefreshToken(Authentication authentication)
      throws UnauthenticatedException {

    User user;
    Object principal = authentication.getPrincipal();
    if (User.class.isAssignableFrom(principal.getClass())) {
      user = (User) principal;
    } else {
      throw new UnauthenticatedException();
    }
    Integer id = user.getId();
    Date currentDate = new Date((new Date()).getTime() + 1 * 1000);
    Date expirationDate = new Date(currentDate.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

    String loginMethod = null;

    if (authentication.getClass().isAssignableFrom(AuthMicrosoftAuthentication.class)) {
      loginMethod = LOGIN_METHOD_MICROSOFT;
    }

    return Jwts.builder()
        .subject(id.toString())
        .claim(PURPOSE_CLAIM_KEY, PURPOSE_REFRESH_TOKEN)
        .claim(LOGIN_METHOD_CLAIM_KEY, loginMethod)
        .issuedAt(currentDate)
        .expiration(expirationDate)
        .signWith(key())
        .compact();
  }
}
