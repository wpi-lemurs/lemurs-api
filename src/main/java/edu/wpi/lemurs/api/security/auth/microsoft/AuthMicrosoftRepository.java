/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.microsoft;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/** The {@link CrudRepository} for a {@link AuthMicrosoft}. */
public interface AuthMicrosoftRepository extends CrudRepository<AuthMicrosoft, String> {
  public Optional<AuthMicrosoft> findByUserID(Integer userID);
}
