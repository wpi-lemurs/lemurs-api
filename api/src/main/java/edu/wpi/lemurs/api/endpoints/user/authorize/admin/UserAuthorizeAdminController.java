/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user.authorize.admin;

import edu.wpi.lemurs.api.exceptions.BadRequestException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.auth.email.elevated.AuthorizedEmailElevatedService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint for authorizing users with permissions. */
@RestController
public class UserAuthorizeAdminController {
  private AuthorizedEmailElevatedService authorizedEmailService;

  /** Autowires a {@link UserAuthorizeAdminController} */
  @Autowired
  public UserAuthorizeAdminController(AuthorizedEmailElevatedService authorizedEmailService) {
    this.authorizedEmailService = authorizedEmailService;
  }

  /**
   * The <code>/user/authorize</code> {@code POST} endpoint authorizes a user for the given user
   * info.
   */
  @PostMapping("/user/authorize/admin")
  public ResponseEntity<Void> authorize(@RequestBody AuthAdminEmailDto authEmailDto) {
    try {
      // TODO: Check whether the email is in user info, and add directly if it is.
      authorizedEmailService.authorize(
          authEmailDto.getEmail(), LemursRole.valueOf(authEmailDto.getRole()));

      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (BadRequestException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
