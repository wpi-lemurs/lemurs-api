/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.progress;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProgressController {

  private ProgressService progressService;

  @Autowired
  public ProgressController(ProgressService progressService) {
    this.progressService = progressService;
  }

  @GetMapping("/progress")
  public ResponseEntity<ProgressResponse> getWeeklySurveys() {
    try {
      ProgressResponse progressResponse = progressService.getProgressResponse();

      return new ResponseEntity<>(progressResponse, HttpStatus.OK);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}
