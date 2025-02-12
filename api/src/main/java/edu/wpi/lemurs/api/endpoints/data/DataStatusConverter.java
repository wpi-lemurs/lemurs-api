/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * A {@link DataStatusConverter} converts between the {@link DataStatus} enum and an integer status
 * representation.
 */
@Converter()
public class DataStatusConverter implements AttributeConverter<DataStatus, Integer> {
  @Override
  public Integer convertToDatabaseColumn(DataStatus status) {
    return (status != null) ? status.getStatus() : -1;
  }

  @Override
  public DataStatus convertToEntityAttribute(Integer status) {
    return DataStatus.valueOf(status);
  }
}
