/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.proximity;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProximityController {
  private ProximityService proximityService;

  @Autowired
  public ProximityController(ProximityService proximityService) {
    this.proximityService = proximityService;
  }

  @PostMapping("/data/proximity")
  public ResponseEntity<Void> saveProximity(@RequestBody ProximityDto proximityDto) {
    try {
      proximityService.recordProximity(proximityDto);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
