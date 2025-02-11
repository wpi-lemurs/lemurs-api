/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.user.umass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link Umass} represents a user to umass id pairing in the app. */
@Table(name = "umass_id")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Umass {
  @Id
  @Column(nullable = false, name = "app_user_id")
  private Integer userId;

  @Column(nullable = false)
  private String umassId;
}
