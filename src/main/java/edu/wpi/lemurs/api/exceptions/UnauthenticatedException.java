/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.exceptions;

import edu.wpi.lemurs.api.security.user.User;

/**
 * An {@link Exception} when no {@link User} is authenticated for a secure request.
 *
 * @fs.httpStatus 401 Unauthorized
 */
public class UnauthenticatedException extends Exception {
  public UnauthenticatedException() {
    super();
  }

  public UnauthenticatedException(Throwable cause) {
    super(cause);
  }

  public UnauthenticatedException(String msg) {
    super(msg);
  }

  public UnauthenticatedException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
