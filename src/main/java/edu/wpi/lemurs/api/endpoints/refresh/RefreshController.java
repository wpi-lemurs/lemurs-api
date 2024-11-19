/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.refresh;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.security.auth.jwt.JwtResponse;
import edu.wpi.lemurs.api.security.auth.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class RefreshController {

  private JwtService jwtService;

  /** Autowires the {@link RefreshController}. */
  @Autowired
  public RefreshController(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  /**
   * The {@code /auth/login} {@code POST} endpoint receives user credentials and returns a JWT
   * token.
   */
  @PostMapping("/auth/refresh")
  public ResponseEntity<JwtResponse> refreshUserAccount(@RequestBody RefreshDto refreshDto) {

    try {
      return new ResponseEntity<>(
          jwtService.useRefreshToken(refreshDto.getRefreshToken()), HttpStatus.OK);
    } catch (BadCredentialsException | UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }
}
