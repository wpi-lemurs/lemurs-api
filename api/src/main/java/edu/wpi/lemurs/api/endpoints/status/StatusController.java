/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.status;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for the danger alerts mailing list. */
@RestController
public class StatusController {

  /** The {@code /status} {@code GET} Checks the status. */
  @GetMapping("/status")
  public ResponseEntity<Status> getStatus() {

    Status status = new Status(true);

    try {
      return new ResponseEntity<>(status, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
