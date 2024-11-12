/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.email;

import org.springframework.data.repository.CrudRepository;

/** The {@link CrudRepository} for a {@link AuthMicrosoft}. */
public interface AuthorizedEmailRepository extends CrudRepository<AuthorizedEmail, String> {}
