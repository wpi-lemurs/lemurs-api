/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.exceptions;

import lombok.experimental.StandardException;

/**
 * An {@link Exception} when an entity does not exist.
 *
 * @fs.httpStatus 404 Not Found
 */
@StandardException
public class EntityDoesNotExistException extends Exception {}
