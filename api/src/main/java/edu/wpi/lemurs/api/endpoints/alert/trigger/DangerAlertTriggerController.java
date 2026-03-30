/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.trigger;

import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DangerAlertTriggerController {

  private final DangerAlertTriggerManagementService service;

  @Autowired
  public DangerAlertTriggerController(DangerAlertTriggerManagementService service) {
    this.service = service;
  }

  @GetMapping ("/danger-alert-triggers")
  public ResponseEntity<List<DangerAlertTriggerDto>> getAllTriggers()
      throws UnauthenticatedException, UnauthorizedException {
    return ResponseEntity.ok(service.getAllTriggers());
  }

  @GetMapping("/danger-alert-triggers-{id}")
  public ResponseEntity<DangerAlertTriggerDto> getTrigger(@PathVariable Integer id)
      throws EntityDoesNotExistException, UnauthenticatedException, UnauthorizedException {
    return ResponseEntity.ok(service.getTrigger(id));
  }

  @PostMapping
  public ResponseEntity<DangerAlertTriggerDto> createTrigger(@RequestBody DangerAlertTriggerDto dto)
      throws UnauthenticatedException, UnauthorizedException {
    return new ResponseEntity<>(service.createTrigger(dto), HttpStatus.CREATED);
  }

  @PutMapping("/danger-alert-triggers-{id}")
  public ResponseEntity<DangerAlertTriggerDto> updateTrigger(
      @PathVariable Integer id, @RequestBody DangerAlertTriggerDto dto)
      throws EntityDoesNotExistException, UnauthenticatedException, UnauthorizedException {
    return ResponseEntity.ok(service.updateTrigger(id, dto));
  }

  @DeleteMapping("/danger-alert-triggers-{id}")
  public ResponseEntity<Void> deleteTrigger(@PathVariable Integer id)
      throws EntityDoesNotExistException, UnauthenticatedException, UnauthorizedException {
    service.deleteTrigger(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<Void> refreshTriggers() {
    try {
      service.refreshTriggers();
      return ResponseEntity.noContent().build();
    } catch (UnauthenticatedException | UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }
}
