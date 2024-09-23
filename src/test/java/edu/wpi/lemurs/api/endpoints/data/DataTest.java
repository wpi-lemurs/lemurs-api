/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.lemurs.api.TestConstants;
import org.junit.jupiter.api.Test;

class DataTest implements TestConstants {

  @Test
  void testData() {
    Data data = new Data(TEST_ID_0, TEST_TYPE_0, TEST_JSON_DATA_0, TEST_STATUS_UNPROCESSED);

    assertEquals(TEST_ID_0, data.getId());
    assertEquals(TEST_TYPE_0, data.getType());
    assertEquals(TEST_JSON_DATA_0, data.getData());
    assertEquals(TEST_STATUS_UNPROCESSED, data.getStatus());
  }
}
