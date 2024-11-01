/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.roles;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link RoleRepository}. */
public interface RoleRepository extends CrudRepository<Role, Role.RoleKey> {

  public Iterable<Role> findByUserId(int userId);
}
