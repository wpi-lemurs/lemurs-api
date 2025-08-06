/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.demographic;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Creates endpoints for getting and answering surveys. */
@RestController
public class DemographicController {

  private DemographicService demographicService;

  @Autowired
  public DemographicController(DemographicService demographicService) {
    this.demographicService = demographicService;
  }

  @GetMapping("/demographic")
  public ResponseEntity<List<DemographicResponse>> getDemographics() {
    try {
      List<DemographicResponse> demographics = demographicService.getDemographics();

      return new ResponseEntity<>(demographics, HttpStatus.OK);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /** The <code>/data</code> {@code POST} endpoint saves the sent demographic. */
  @PostMapping("/demographic")
  public ResponseEntity<Void> recordAnswersWeekly(
      @RequestBody List<DemographicDto> demographicDto) {
    try {
      demographicService.recordDemographic(demographicDto);

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
