/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link UserRepository}. */
public interface UserRepository extends CrudRepository<User, Integer> {}
