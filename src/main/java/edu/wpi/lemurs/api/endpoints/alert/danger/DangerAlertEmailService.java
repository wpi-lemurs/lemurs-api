/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.danger;

import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.endpoints.user.UserService;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import edu.wpi.lemurs.api.services.email.EmailService;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

/** The {@link DangerAlertEmailService} is a service that allows for {@link User} management. */
@Service
public class DangerAlertEmailService {

  private SecurityService securityService;
  private DangerAlertEmailRepository dangerAlertEmailRepository;
  private EmailService emailService;
  private UserService userService;

  /** Autowires a {@link DangerAlertEmailService}. */
  public DangerAlertEmailService(
      SecurityService securityService,
      DangerAlertEmailRepository dangerAlertEmailRepository,
      EmailService emailService,
      UserService userService) {
    this.securityService = securityService;
    this.dangerAlertEmailRepository = dangerAlertEmailRepository;
    this.emailService = emailService;
    this.userService = userService;
  }

  /**
   * Gets all of the emails that should be alerted if someone is in danger.
   *
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.STAFF}
   *     permissions.
   */
  public List<DangerAlertEmail> getEmails() throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.STAFF);

    ArrayList<DangerAlertEmail> emails = new ArrayList<>();

    for (DangerAlertEmail email : dangerAlertEmailRepository.findAll()) {
      emails.add(email);
    }

    return emails;
  }

  /**
   * Adds an email address to the mailing list.
   *
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.STAFF}
   *     permissions.
   */
  public void addEmail(String email) throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.STAFF);

    dangerAlertEmailRepository.save(new DangerAlertEmail(email));
  }

  /**
   * Removes an email address from the mailing list.
   *
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.STAFF}
   *     permissions.
   */
  public void removeEmail(String email) throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.STAFF);

    dangerAlertEmailRepository.deleteById(email);
  }

  /**
   * Sends a danger alert to relevant parties for the given user.
   *
   * @param userID The user that is in danger.
   * @param reasons A list of for the alert.
   * @throws MessagingException Thrown if the email content creation fails.
   * @throws UnsupportedEncodingException Thrown if the email creation for the sender fails.
   * @throws MailException Thrown if the email failed to send.
   * @throws EntityDoesNotExistException Thrown if no user with this id exists.
   * @apiNote This does not check for authorization.
   */
  public void sendAlertWithoutAuthCheck(Integer userID, List<String> reasons)
      throws MessagingException,
          UnsupportedEncodingException,
          MailException,
          EntityDoesNotExistException {

    User user = userService.getUserWithoutAuthCheck(userID);

    String out =
        "The student with UMass REDCap ID '"
            + user.getUmassId()
            + "' is showing concerning signs."
            + "\n\nHere are the following concerning signs:\n";

    for (String reason : reasons) {
      out += "\n - " + reason;
    }

    out += "\n\nLEMURS Team";

    for (DangerAlertEmail address : dangerAlertEmailRepository.findAll()) {
      emailService.sendEmailWithoutAuthorization(
          address.getEmail(), "Danger Alert for Student", out);
    }
  }
}
