/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A {@link DataDto} represents a new {@link Data}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataDto {
  private String type;
  private JsonNode data;
}
