/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.exceptions;

/**
 * An {@link Exception} when an entity does not exist.
 *
 * @fs.httpStatus 404 Not Found
 */
public class EntityDoesNotExistException extends Exception {
  public EntityDoesNotExistException() {
    super();
  }

  public EntityDoesNotExistException(Throwable cause) {
    super(cause);
  }

  public EntityDoesNotExistException(String msg) {
    super(msg);
  }

  public EntityDoesNotExistException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
