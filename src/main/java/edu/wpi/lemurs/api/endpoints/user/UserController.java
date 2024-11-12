/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;

/** Creates an endpoint for adding/editing users. */
@RestController
public class UserController {
  private UserService userService;

  /** Autowires a {@link UserController} */
  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /** The <code>/user</code> {@code POST} endpoint creates a user for the given user info. */
  @PostMapping("/user")
  public ResponseEntity<Void> saveData(@RequestBody UserDto userDto) {
    try {
      userService.createUser(userDto);

      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (Exception e) {
      // TODO: Check for unique contraint failure, return 409 bad request in that scenario.
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
