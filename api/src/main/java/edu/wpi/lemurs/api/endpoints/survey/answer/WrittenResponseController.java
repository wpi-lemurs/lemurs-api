/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import edu.wpi.lemurs.api.endpoints.data.DataController;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Creates an endpoint for posting data. */
@RestController
public class WrittenResponseController {

  private static final Logger logger = LoggerFactory.getLogger(WrittenResponseController.class);

  private WrittenResponseService writingResponseService;

  /** Autowires a {@link DataController} */
  // constructor to autowire the service
  @Autowired
  public WrittenResponseController(WrittenResponseService writingResponseService) {
    this.writingResponseService = writingResponseService;
  }

  /** The <code>/data</code> {@code POST} endpoint saves the sent data. */
  @PostMapping("/data/text")
  public ResponseEntity<Void> recordWritingData(
      @RequestBody WrittenResponseDto writingResponseDto) {
    try {
      writingResponseService.saveWritingData(writingResponseDto);
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
