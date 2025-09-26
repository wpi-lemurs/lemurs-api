/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.health.calorie;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Creates an endpoint for posting calorie data. */
@RestController
public class CalorieController {
  private final CalorieService calorieService;

  /** Autowires a {@link CalorieController} */
  @Autowired
  public CalorieController(CalorieService calorieService) {
    this.calorieService = calorieService;
  }

  /** The <code>/calories</code> {@code POST} endpoint saves the sent calorie data. */
  @PostMapping("/data/calories")
  public ResponseEntity<Void> saveCalories(@RequestBody CalorieDto calorieDto) {
    try {
      calorieService.recordCalories(calorieDto);

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
