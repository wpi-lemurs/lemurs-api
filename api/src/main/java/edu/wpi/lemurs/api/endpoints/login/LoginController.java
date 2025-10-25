/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.login;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.security.auth.jwt.JwtResponse;
import edu.wpi.lemurs.api.security.auth.jwt.JwtService;
import edu.wpi.lemurs.api.security.auth.microsoft.AuthMicrosoftService;
import edu.wpi.lemurs.api.security.auth.microsoft.MicrosoftLoginDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

  private JwtService jwtService;
  private AuthMicrosoftService authMicrosoftService;
  private static final String ACCESS_TOKEN_COOKIE = "access_token";
  private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

  @Value("${app.security.cookie-secure:true}")
  private boolean cookieSecure;

  @Autowired
  public LoginController(JwtService jwtService, AuthMicrosoftService authMicrosoftService) {
    this.jwtService = jwtService;
    this.authMicrosoftService = authMicrosoftService;
  }

  @PostMapping("/auth/login")
  public ResponseEntity<JwtResponse> loginUserAccount(
      @RequestBody MicrosoftLoginDto microsoftLoginDto, HttpServletResponse response) {
    try {
      Authentication tempAuthentication =
          authMicrosoftService.login(microsoftLoginDto.getAccessToken());

      JwtResponse jwtAuthResponse = jwtService.getJwtResponse(tempAuthentication);
      String accessToken = jwtAuthResponse.getAccessToken();
      String refreshToken = jwtAuthResponse.getRefreshToken();

      long accessDurationInSeconds = 3600;
      long refreshDurationInSeconds = 1209600; // 14 days

      ResponseCookie accessCookie =
          ResponseCookie.from(ACCESS_TOKEN_COOKIE, accessToken)
              .httpOnly(true)
              .secure(this.cookieSecure)
              .path("/")
              .maxAge(accessDurationInSeconds)
              .sameSite("Lax")
              .build();
      response.addHeader("Set-Cookie", accessCookie.toString());

      ResponseCookie refreshCookie =
          ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
              .httpOnly(true)
              .secure(this.cookieSecure)
              .path("/")
              .maxAge(refreshDurationInSeconds)
              .sameSite("Lax")
              .build();
      response.addHeader("Set-Cookie", refreshCookie.toString());

      return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    } catch (BadCredentialsException | UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/auth/refresh")
  public ResponseEntity<Void> refreshAccessToken(
      @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
      HttpServletResponse response) {
    if (refreshToken == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    try {
      JwtResponse newJwtResponse = jwtService.refreshJwtResponse(refreshToken);
      String newAccessToken = newJwtResponse.getAccessToken();

      long accessDurationInSeconds = 3600; // 1 hour
      ResponseCookie accessCookie =
          ResponseCookie.from(ACCESS_TOKEN_COOKIE, newAccessToken)
              .httpOnly(true)
              .secure(this.cookieSecure)
              .path("/")
              .maxAge(accessDurationInSeconds)
              .sameSite("Lax")
              .build();
      response.addHeader("Set-Cookie", accessCookie.toString());

      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @GetMapping("/api/validate")
  public ResponseEntity<Void> validate(
      @CookieValue(name = ACCESS_TOKEN_COOKIE, required = false) String token) {
    if (token == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    try {
      jwtService.assertValidAccessToken(token);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/auth/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    ResponseCookie accessCookie =
        ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
            .httpOnly(true)
            .secure(this.cookieSecure)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();
    response.addHeader("Set-Cookie", accessCookie.toString());

    ResponseCookie refreshCookie =
        ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
            .httpOnly(true)
            .secure(this.cookieSecure)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();
    response.addHeader("Set-Cookie", refreshCookie.toString());

    return new ResponseEntity<>(HttpStatus.OK);
  }
}