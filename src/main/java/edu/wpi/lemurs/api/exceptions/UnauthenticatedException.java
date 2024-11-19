/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.exceptions;

import edu.wpi.lemurs.api.endpoints.user.User;
import lombok.experimental.StandardException;

/**
 * An {@link Exception} when no {@link User} is authenticated for a secure request.
 *
 * @fs.httpStatus 401 Unauthorized
 */
@StandardException
public class UnauthenticatedException extends Exception {}
