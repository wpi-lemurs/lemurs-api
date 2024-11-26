/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.test.email.danger;

import edu.wpi.lemurs.api.endpoints.alert.danger.DangerAlertEmailService;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// TODO: Remove in production, or disable when in production.
/** A endpoint for sending an danger alert email. This is for testing purposes only. */
@RestController
public class TestEmailDangerController {

  private SecurityService securityService;
  private DangerAlertEmailService dangerAlertEmailService;

  /** Autowires a {@link TestEmailDangerController} */
  @Autowired
  public TestEmailDangerController(
      SecurityService securityService, DangerAlertEmailService dangerAlertEmailService) {
    this.securityService = securityService;
    this.dangerAlertEmailService = dangerAlertEmailService;
  }

  /** The <code>/test/email</code> {@code POST} endpoint sends a test email. */
  @PostMapping("/test/email/danger")
  public ResponseEntity<Void> saveData(@RequestBody TestEmailDangerDto testEmailDangerDto) {
    try {
      securityService.assertHasPermission(LemursRole.STAFF);
      dangerAlertEmailService.sendAlertWithoutAuthCheck(
          testEmailDangerDto.getUserID(), testEmailDangerDto.getReasons());

      return new ResponseEntity<>(HttpStatus.OK);
    } catch (EntityDoesNotExistException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (MailException | UnsupportedEncodingException | MessagingException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
