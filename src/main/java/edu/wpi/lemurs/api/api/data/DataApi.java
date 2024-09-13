/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.api.data;

import edu.wpi.lemurs.api.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Creates an endpoint for posting data. */
@RestController
public class DataApi {

  private DataService dataService;

  /** Autowires a {@link DataApi} */
  @Autowired
  public DataApi(DataService dataService) {
    this.dataService = dataService;
  }

  /** The <code>/data</code> {@code POST} endpoint saves the sent data. */
  @PostMapping("/data")
  public ResponseEntity<Void> saveData(@RequestBody DataDto dataDto) {
    try {
      dataService.saveData(dataDto);

      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
