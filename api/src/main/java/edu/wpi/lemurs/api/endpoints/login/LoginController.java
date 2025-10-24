/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.login;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.security.auth.jwt.JwtResponse;
import edu.wpi.lemurs.api.security.auth.jwt.JwtService;
import edu.wpi.lemurs.api.security.auth.microsoft.AuthMicrosoftService;
import edu.wpi.lemurs.api.security.auth.microsoft.MicrosoftLoginDto;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
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

  @Value("${app.security.cookie-secure:true}") // Injects from application.properties
  private boolean cookieSecure;

  @Autowired
  public LoginController(JwtService jwtService, AuthMicrosoftService authMicrosoftService) {
    this.jwtService = jwtService;
    this.authMicrosoftService = authMicrosoftService;
  }

  @PostMapping("/auth/login")
  public ResponseEntity<JwtResponse> loginUserAccount(
      @RequestBody MicrosoftLoginDto microsoftLoginDto,
      HttpServletResponse response // <-- Add HttpServletResponse
  ) {
    try {
      Authentication tempAuthentication =
          authMicrosoftService.login(microsoftLoginDto.getAccessToken());

      JwtResponse jwtAuthResponse = jwtService.getJwtResponse(tempAuthentication);

      String accessToken = jwtAuthResponse.getAccessToken();
      long durationInSeconds =
          TimeUnit.MILLISECONDS.toSeconds(jwtService.getJwtExpirationMs(accessToken));

      ResponseCookie cookie =
          ResponseCookie.from(ACCESS_TOKEN_COOKIE, accessToken)
              .httpOnly(true)
              .secure(this.cookieSecure) // Dynamic value from properties
              .path("/")
              .maxAge(durationInSeconds)
              .sameSite("Lax") // For CSRF protection
              .build();

      response.addHeader("Set-Cookie", cookie.toString());

      // This is unchanged and still returns the token for React
      return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    } catch (BadCredentialsException | UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  /**
   * The {@code /api/validate} {@code GET} endpoint receives a request from the NGINX proxy and
   * validates the JWT token FROM THE COOKIE.
   */
  @GetMapping("/api/validate")
  public ResponseEntity<Void> validate(
      // Read the token from the cookie, not the header
      @CookieValue(name = ACCESS_TOKEN_COOKIE, required = false) String token
  ) {
    if (token == null) { // Check for null token
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    try {
      jwtService.assertValidAccessToken(token);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  /**
   * The {@code /auth/logout} {@code POST} endpoint clears the access_token cookie. React must call
   * this on logout.
   */
  @PostMapping("/auth/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    // Build a cookie that instructs the browser to delete the existing one
    ResponseCookie cookie =
        ResponseCookie.from(ACCESS_TOKEN_COOKIE, "") // Empty value
            .httpOnly(true)
            .secure(this.cookieSecure) // Must match the properties of the login cookie
            .path("/")
            .maxAge(0) // <-- Tells the browser to expire it immediately
            .sameSite("Lax")
            .build();

    response.addHeader("Set-Cookie", cookie.toString());
    return new ResponseEntity<>(HttpStatus.OK);
  }
}