/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.roles;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The {@link RoleService} is a service that allows for {@link Role} management. */
@Service
@Transactional
public class RoleService {

  private RoleRepository roleRepository;

  /** Autowires a {@link RoleService}. */
  @Autowired
  public RoleService(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  /**
   * Gets all of the roles for the user.
   *
   * @param userID The user's id.
   * @return The user.
   */
  public List<LemursRole> getRoles(int userID) {

    List<LemursRole> roles = new ArrayList<>();

    for (Role role : roleRepository.findByUserId(userID)) {
      roles.add(role.getLemursRole());
    }

    return roles;
  }

  /**
   * Determines if the user has the appropriate role.
   *
   * @param userID The user's id.
   * @param role The role to check for.
   * @return Whether the user has the role.
   */
  public boolean hasRole(int userID, LemursRole role) {
    List<LemursRole> roles = getRoles(userID);

    return roles.contains(role);
  }

  /**
   * Determins if the user has at least the permission of the role.
   *
   * @param userID The user's id.
   * @param role The role to check for.
   * @return Whether the user has the role's permission.
   */
  public boolean hasPermission(int userID, LemursRole role) {
    List<LemursRole> roles = getRoles(userID);

    for (LemursRole r : roles) {
      if (role.getPermission() <= r.getPermission()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Adds a role to a user.
   *
   * @param userID The user's id.
   * @param lemursRole The role to add.
   * @apiNote This service method does not check the caller's permissions.
   */
  public void addRoleWithoutAuthCheck(Integer userID, LemursRole lemursRole) {
    roleRepository.save(new Role(userID, lemursRole));
  }
}
