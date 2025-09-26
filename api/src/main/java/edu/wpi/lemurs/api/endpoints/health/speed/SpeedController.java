/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.speed;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Creates an endpoint for posting speed data. */
@RestController
public class SpeedController {

  private final SpeedService speedService;

  /** Autowires a {@link SpeedController} */
  @Autowired
  public SpeedController(SpeedService speedService) {
    this.speedService = speedService;
  }

  /** The <code>/speed</code> {@code POST} endpoint saves the sent speed data. */
  @PostMapping("/data/speed")
  public ResponseEntity<Void> saveSpeed(@RequestBody SpeedDto speedDto) {
    try {
      speedService.recordSpeed(speedDto);
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
