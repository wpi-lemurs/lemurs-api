/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.exceptions;

import lombok.experimental.StandardException;

/**
 * An {@link Exception} when a request does not follow api rules.
 *
 * @fs.httpStatus 400 Bad Request
 */
@StandardException
public class BadRequestException extends Exception {}
