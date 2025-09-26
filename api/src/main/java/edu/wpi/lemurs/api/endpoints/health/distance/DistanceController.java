/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.distance;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Creates an endpoint for posting distance data. */
@RestController
public class DistanceController {

  private final DistanceService distanceService;

  /** Autowires a {@link DistanceController} */
  @Autowired
  public DistanceController(DistanceService distanceService) {
    this.distanceService = distanceService;
  }

  /** The <code>/distance</code> {@code POST} endpoint saves the sent distance data. */
  @PostMapping("/data/distance")
  public ResponseEntity<Void> saveDistance(@RequestBody DistanceDto distanceDto) {
    try {
      distanceService.recordDistance(distanceDto);

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
