/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.login;

import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.security.auth.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class LoginController {

  private JwtService jwtService;

  /** Autowires the {@link LoginController}. */
  @Autowired
  public LoginController(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  /**
   * The {@code /auth/login} {@code POST} endpoint receives user credentials and returns a JWT
   * token.
   */
  @PostMapping("/auth/login")
  public ResponseEntity<JwtResponse> registerUserAccount(@RequestBody LoginDto loginDto) {

    try {
      // TODO: This is just for testing the JWT.  Currently just logs in as the request user id.
      Authentication tempAuthentication =
          new UsernamePasswordAuthenticationToken(
              new User(loginDto.getUserID(), null, false, false),
              null,
              AuthorityUtils.NO_AUTHORITIES);

      String token = jwtService.generateToken(tempAuthentication);

      JwtResponse jwtAuthResponse = new JwtResponse();
      jwtAuthResponse.setAccessToken(token);

      return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    } catch (BadCredentialsException | UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }
}
