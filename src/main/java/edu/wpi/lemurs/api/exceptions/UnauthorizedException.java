/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.exceptions;

import edu.wpi.lemurs.api.endpoints.user.User;

/**
 * An {@link Exception} when the {@link User} is not authorized for the request.
 *
 * @fs.httpStatus 403 Unauthorized
 */
public class UnauthorizedException extends Exception {
  public UnauthorizedException() {
    super();
  }

  public UnauthorizedException(Throwable cause) {
    super(cause);
  }

  public UnauthorizedException(String msg) {
    super(msg);
  }

  public UnauthorizedException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
