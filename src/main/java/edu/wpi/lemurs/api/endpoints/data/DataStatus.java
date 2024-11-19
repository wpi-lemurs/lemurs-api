/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/** A {@link DataStatus} represents the status of data. */
@Getter
public enum DataStatus {

  /** The data status is unknown. */
  UNKNOWN(-1),

  /** The data has not been processed. */
  NOT_PROCESSED(0);

  private static final Map<Integer, DataStatus> INT_TO_STATUS = new HashMap<>();

  static {
    for (DataStatus dataStatus : values()) {
      INT_TO_STATUS.put(dataStatus.status, dataStatus);
    }
  }

  private int status;

  private DataStatus(int status) {
    this.status = status;
  }

  /**
   * Gets the {@link DataStatus} for the given status value. If an invalid status is given, returns
   * {@code DataStatus.UNKNOWN}.
   */
  public static DataStatus valueOf(int status) {
    DataStatus dataStatus = INT_TO_STATUS.get(status);
    if (dataStatus == null) {
      return DataStatus.UNKNOWN;
    }
    return dataStatus;
  }
}
