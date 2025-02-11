/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.email.elevated;

import edu.wpi.lemurs.api.security.auth.email.elevated.AuthorizedEmailElevated.AuthorizedEmailElevatedKey;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import edu.wpi.lemurs.api.security.roles.LemursRoleConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An {@link AuthorizedEmailElevated} represents an email address that will be given higher
 * permissions.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(AuthorizedEmailElevatedKey.class)
public class AuthorizedEmailElevated {

  @Id private String email;

  @Id private LemursRole lemursRole;

  @Column(nullable = false)
  private Date expiration;

  /** The compound primary key for the {@link AuthorizedEmailElevated} class. */
  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public static class AuthorizedEmailElevatedKey implements Serializable {
    @Column(nullable = false)
    private String email;

    @Column(nullable = false, name = "role")
    @Convert(converter = LemursRoleConverter.class)
    private LemursRole lemursRole;
  }
}
