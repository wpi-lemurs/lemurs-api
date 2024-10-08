package edu.wpi.lemurs.api.security.roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

/** The {@link RoleService} is a service that allows for {@link Role} management. */
@Service
@Transactional
public class RoleService {
  
  private RoleRepository roleRepository;

  @Autowired
  public RoleService(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  /**
   * Gets all of the roles for the user.
   * @param userId The user's id.
   * @return The user.
   */
  public List<LemursRole> getRoles(int userId) {
    
    List<LemursRole> roles = new ArrayList<>();

    for (Role role : roleRepository.findByAppUserId(userId)) {
      roles.add(role.getLemursRole());
    }
    
    return roles;
  }
}
