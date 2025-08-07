/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.trigger;

import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/danger-alert-triggers")
public class DangerAlertTriggerController {

  private final DangerAlertTriggerManagementService service;

  @Autowired
  public DangerAlertTriggerController(DangerAlertTriggerManagementService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<List<DangerAlertTriggerDto>> getAllTriggers() {
    return ResponseEntity.ok(service.getAllTriggers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<DangerAlertTriggerDto> getTrigger(@PathVariable Integer id) throws EntityDoesNotExistException {
    return ResponseEntity.ok(service.getTrigger(id));
  }

  @PostMapping
  public ResponseEntity<DangerAlertTriggerDto> createTrigger(@RequestBody DangerAlertTriggerDto dto) {
    return new ResponseEntity<>(service.createTrigger(dto), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<DangerAlertTriggerDto> updateTrigger(
      @PathVariable Integer id, @RequestBody DangerAlertTriggerDto dto) throws EntityDoesNotExistException {
    return ResponseEntity.ok(service.updateTrigger(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTrigger(@PathVariable Integer id) throws EntityDoesNotExistException {
    service.deleteTrigger(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<Void> refreshTriggers() {
    service.getTriggerService().refreshTriggers();
    return ResponseEntity.noContent().build();
  }
}
