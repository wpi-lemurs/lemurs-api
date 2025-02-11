/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.email.elevated;

import edu.wpi.lemurs.api.security.auth.email.elevated.AuthorizedEmailElevated.AuthorizedEmailElevatedKey;
import edu.wpi.lemurs.api.security.auth.microsoft.AuthMicrosoft;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/** The {@link CrudRepository} for a {@link AuthMicrosoft}. */
public interface AuthorizedEmailElevatedRepository
    extends CrudRepository<AuthorizedEmailElevated, AuthorizedEmailElevatedKey> {
  public List<AuthorizedEmailElevated> findByEmail(String email);
}
