/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.test.email;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import edu.wpi.lemurs.api.services.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// TODO: Remove in production, or disable when in production.
/** A endpoint for sending an email. This is for testing purposes only. */
@RestController
public class TestEmailController {

  private SecurityService securityService;
  private EmailService emailService;

  /** Autowires a {@link TestEmailController} */
  @Autowired
  public TestEmailController(SecurityService securityService, EmailService emailService) {
    this.securityService = securityService;
    this.emailService = emailService;
  }

  /** The <code>/test/email</code> {@code POST} endpoint sends a test email. */
  @PostMapping("/test/email")
  public ResponseEntity<Void> saveData(@RequestBody TestEmailDto emailDto) {
    try {
      securityService.assertHasPermission(LemursRole.STAFF);

      emailService.sendEmailWithoutAuthorization(
          emailDto.getTo(), "Test email!", "Sent a test email!");

      return new ResponseEntity<>(HttpStatus.OK);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
