/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.exceptions;

/**
 * An {@link Exception} when an external communication fails.
 *
 * @fs.httpStatus 500 Internal Server Error
 */
public class BadExternalCommunicationException extends Exception {
  public BadExternalCommunicationException() {
    super();
  }

  public BadExternalCommunicationException(Throwable cause) {
    super(cause);
  }

  public BadExternalCommunicationException(String msg) {
    super(msg);
  }

  public BadExternalCommunicationException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
