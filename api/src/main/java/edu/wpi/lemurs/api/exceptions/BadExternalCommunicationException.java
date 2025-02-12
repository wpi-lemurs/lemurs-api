/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.exceptions;

import lombok.experimental.StandardException;

/**
 * An {@link Exception} when an external communication fails.
 *
 * @fs.httpStatus 500 Internal Server Error
 */
@StandardException
public class BadExternalCommunicationException extends Exception {}
