package edu.wpi.lemurs.api.endpoints.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.wpi.lemurs.api.TestConstants;

public class DataTest implements TestConstants {
  
  @Test
  public void testData() {
    Data data = new Data(TEST_ID_0, TEST_TYPE_0, TEST_JSON_DATA_0, TEST_STATUS_UNPROCESSED);

    assertEquals(data.getId(), TEST_ID_0);
    assertEquals(data.getType(), TEST_TYPE_0);
    assertEquals(data.getData(), TEST_JSON_DATA_0);
    assertEquals(data.getStatus(), TEST_STATUS_UNPROCESSED);
  }
}
