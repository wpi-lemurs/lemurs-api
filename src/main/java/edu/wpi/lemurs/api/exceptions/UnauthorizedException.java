/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.exceptions;

import edu.wpi.lemurs.api.endpoints.user.User;
import lombok.experimental.StandardException;

/**
 * An {@link Exception} when the {@link User} is not authorized for the request.
 *
 * @fs.httpStatus 403 Unauthorized
 */
@StandardException
public class UnauthorizedException extends Exception {}
