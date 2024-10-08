package edu.wpi.lemurs.api.security.roles;

import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.HashMap;

/**
 * Represents a role for the LEMURS system.
 */
public enum LemursRole implements GrantedAuthority {

  /** A roleless can do nothing. */
  ROLELESS("roleless", -1),

  /** A user can input data to the system. */
  USER("user", 0),

  /** A researcher can view data in the system WITHOUT knowing who the data is referring to. */
  RESEARCHER("researcher", 1),

  /** A staff can connect a user id to PII (personal identifiable information) and add new users. */
  STAFF("staff", 2),

  /** An owner can add other users as staff or researchers. */
  OWNER("owner", 3);

  private static final Map<Integer, LemursRole> INT_TO_ROLE = new HashMap<>();

  static {
    for (LemursRole role : values()) {
      INT_TO_ROLE.put(role.permission, role);
    }
  }

  /**
   * Gets the {@link LemursRole} for the given permission value. If an invalid permission is given, returns
   * {@code DataStatus.ROLELESS}.
   */
  public static LemursRole valueOf(int permission) {
    LemursRole role = INT_TO_ROLE.get(permission);
    if (role == null) {
      return LemursRole.ROLELESS;
    }
    return role;
  }

  private String representation;
  private int permission;

  /**
   * Creates a role with a string representation and permissions level.
   * @param representation The string representation.
   * @param permission The permission level.
   */
  LemursRole(String representation, int permission) {
    this.representation = representation;
    this.permission = permission;
  }

  @Override
  public String getAuthority() {
    return representation;
  }

  /**
   * Returns the role's permisison.
   * @return The role's permission level.
   */
  public int getPermission() {
    return this.permission;
  }
}
