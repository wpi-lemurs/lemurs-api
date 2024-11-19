/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user.authorize;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.auth.email.AuthorizedEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint for authorizing users. */
@RestController
public class UserAuthorizeController {
  private AuthorizedEmailService authorizedEmailService;

  /** Autowires a {@link UserAuthorizeController} */
  @Autowired
  public UserAuthorizeController(AuthorizedEmailService authorizedEmailService) {
    this.authorizedEmailService = authorizedEmailService;
  }

  /**
   * The <code>/user/authorize</code> {@code POST} endpoint authorizes a user for the given user
   * info.
   */
  @PostMapping("/user/authorize")
  public ResponseEntity<Void> authorize(@RequestBody AuthEmailDto authEmailDto) {
    try {
      authorizedEmailService.authorize(authEmailDto.getEmail(), authEmailDto.getUmassId());

      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
