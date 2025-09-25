/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.distance;

import edu.wpi.lemurs.api.endpoints.data.DataStatus;
import edu.wpi.lemurs.api.endpoints.data.DataStatusConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
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
public class Distance {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Integer id;

  @Column(nullable = false, name = "app_user_id")
  private Integer userId;

  @Column(nullable = false)
  private Long distance;

  @Column(nullable = false)
  private LocalDateTime start_timestamp;

  @Column(nullable = false)
  private LocalDateTime end_timestamp;

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
