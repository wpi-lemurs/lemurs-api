/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.step;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Creates an endpoint for posting step data. */
@RestController
public class StepController {

  private final StepService stepService;

  /** Autowires a {@link StepController} */
  @Autowired
  public StepController(StepService stepService) {
    this.stepService = stepService;
  }

  /** The <code>/steps</code> {@code POST} endpoint saves the sent step data. */
  @PostMapping("/data/steps")
  public ResponseEntity<Void> saveSteps(@RequestBody StepDto stepDto) {
    try {
      stepService.recordSteps(stepDto);

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
