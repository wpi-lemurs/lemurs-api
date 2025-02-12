/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api;

import edu.wpi.lemurs.api.endpoints.data.DataStatus;

public interface TestConstants {
  public static Integer TEST_ID_0 = 3;
  public static String TEST_UMASS_ID_0 = "5381";
  public static String TEST_TYPE_0 = "new-type";
  public static String TEST_JSON_DATA_0 = "{'data':'data'}";
  public static DataStatus TEST_STATUS_UNPROCESSED = DataStatus.NOT_PROCESSED;
}
