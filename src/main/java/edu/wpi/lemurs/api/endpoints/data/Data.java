/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import edu.wpi.lemurs.api.status.DataStatus;
import edu.wpi.lemurs.api.status.DataStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** A {@link Data} represents a single unit of generic data. */
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Data {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Integer id;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private String data;

  @Column(nullable = false)
  @Convert(converter = DataStatusConverter.class)
  private DataStatus status;
}
