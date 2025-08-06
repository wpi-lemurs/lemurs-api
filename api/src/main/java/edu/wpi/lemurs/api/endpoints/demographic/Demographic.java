/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.demographic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link Demographic} represents a demographic. */
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@IdClass(edu.wpi.lemurs.api.endpoints.demographic.Demographic.DemographicKey.class)
public class Demographic {
  @Id private Integer userID;

  @Id private String keyword;

  @Column(nullable = false)
  private String value;

  /** The compound primary key for the {@link Demographic} class. */
  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public static class DemographicKey implements Serializable {
    @Column(nullable = false, name = "app_user_id")
    private Integer userID;

    @Column(nullable = false)
    private String keyword;
  }
}
