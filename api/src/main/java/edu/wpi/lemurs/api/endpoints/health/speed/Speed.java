/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.speed;

import edu.wpi.lemurs.api.endpoints.data.DataStatus;
import edu.wpi.lemurs.api.endpoints.data.DataStatusConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Speed {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Integer id;

  @Column(nullable = false)
  private Integer app_user_id;

  @Column(nullable = false)
  @JdbcTypeCode(SqlTypes.ARRAY)
  private List<Double> speed;

  @Column(nullable = false)
  private LocalDateTime start_timestamp;

  @Column(nullable = false)
  private LocalDateTime end_timestamp;

  @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'm/s'")
  private String unit;

  @Column(nullable = false)
  private String appSource;

  @Column(nullable = false)
  private Date recordedDate;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false, name = "additional_data")
  @JdbcTypeCode(SqlTypes.JSON)
  private String data;

  @Column(nullable = false)
  @Convert(converter = DataStatusConverter.class)
  private DataStatus status;
}
