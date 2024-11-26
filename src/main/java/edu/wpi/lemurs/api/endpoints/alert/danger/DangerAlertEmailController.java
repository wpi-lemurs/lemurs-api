/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.danger;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller for the danger alerts mailing list. */
@RestController
public class DangerAlertEmailController {

  private DangerAlertEmailService dangerAlertEmailService;

  /** Autowires a {@link DangerAlertEmailController}. */
  public DangerAlertEmailController(DangerAlertEmailService dangerAlertEmailService) {
    this.dangerAlertEmailService = dangerAlertEmailService;
  }

  /** The {@code /alert/danger} {@code GET} gets all of the emails on the mailing list. */
  @GetMapping("/alert/danger")
  public ResponseEntity<List<DangerAlertEmail>> getEmails() {

    try {
      return new ResponseEntity<>(dangerAlertEmailService.getEmails(), HttpStatus.CREATED);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /** The {@code /alert/danger} {@code POST} adds an email to the mailing list. */
  @PostMapping("/alert/danger")
  public ResponseEntity<Void> addEmail(@RequestBody DangerAlertEmailDto dangerAlertEmailDto) {

    try {
      dangerAlertEmailService.addEmail(dangerAlertEmailDto.getEmail());

      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /** The {@code /alert/danger} {@code DELETE} removes an email to the mailing list. */
  @DeleteMapping("/alert/danger")
  public ResponseEntity<Void> deleteEmail(@RequestBody DangerAlertEmailDto dangerAlertEmailDto) {

    try {
      dangerAlertEmailService.removeEmail(dangerAlertEmailDto.getEmail());

      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  // TODO: Create a way to delete yourself from a mailing list.
}
