package edu.wpi.lemurs.api.security.roles;

import edu.wpi.lemurs.api.security.roles.Role.RoleKey;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/** A {@link Role} represents a role that a user has. */
@Table(name="app_role")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@IdClass(RoleKey.class)
public class Role {
  @Id
  @Column(nullable = false, name = "app_user_id")
  private Integer userId;

  @Id
  @Column(nullable = false, name = "role")
  @Convert(converter = LemursRoleConverter.class)
  private LemursRole lemursRole;

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  static class RoleKey implements Serializable {
    private Integer userId;
    private LemursRole lemursRole;
  }
}
