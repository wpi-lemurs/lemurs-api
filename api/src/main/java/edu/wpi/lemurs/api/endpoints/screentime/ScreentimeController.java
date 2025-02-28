/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.screentime;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Creates an endpoint for posting data. */
@RestController
public class ScreentimeController {

  private ScreentimeService screentimeService;

  /** Autowires a {@link ScreentimeController} */
  @Autowired
  public ScreentimeController(ScreentimeService screentimeService) {
    this.screentimeService = screentimeService;
  }

  /** The <code>/screentime</code> {@code POST} endpoint saves the sent screentime data. */
  @PostMapping("/screentime")
  public ResponseEntity<Void> recordAnswersDaily(@RequestBody ScreentimeDto screentimeDto) {
    try {
      screentimeService.recordScreentime(screentimeDto);

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
