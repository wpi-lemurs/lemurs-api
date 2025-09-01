/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.danger;

import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.endpoints.user.UserService;
import edu.wpi.lemurs.api.endpoints.user.info.UserInfo;
import edu.wpi.lemurs.api.endpoints.user.info.UserInfoService;
import edu.wpi.lemurs.api.endpoints.user.umass.UmassService;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import edu.wpi.lemurs.api.services.email.EmailService;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

/** The {@link DangerAlertEmailService} is a service that allows for {@link User} management. */
@Service
public class DangerAlertEmailService {

  private SecurityService securityService;
  private DangerAlertEmailRepository dangerAlertEmailRepository;
  private EmailService emailService;
  private UserService userService;
  private UmassService umassService;
  private UserInfoService userInfoService;

  /** Autowires a {@link DangerAlertEmailService}. */
  public DangerAlertEmailService(
      SecurityService securityService,
      DangerAlertEmailRepository dangerAlertEmailRepository,
      EmailService emailService,
      UserService userService,
      UmassService umassService,
      UserInfoService userInfoService) {
    this.securityService = securityService;
    this.dangerAlertEmailRepository = dangerAlertEmailRepository;
    this.emailService = emailService;
    this.userService = userService;
    this.umassService = umassService;
    this.userInfoService = userInfoService;
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

    return StreamSupport.stream(dangerAlertEmailRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
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

    Optional<String> umassID = umassService.getUmassID(userID);
    // TODO: TBD the best solution for this.
    Optional<UserInfo> userInfo = userInfoService.getUserInfo(userID);

    if (umassID.isEmpty()) {
      return;
    }

    StringBuilder out =
        new StringBuilder("The student with UMass REDCap ID '")
            .append(umassID.get())
            .append("' is showing concerning signs.")
            .append("\n\nHere are the following concerning signs:\n");

    for (String reason : reasons) {
      out.append("\n - ").append(reason);
    }

    out.append("\n\nLEMURS Team");

    for (DangerAlertEmail address : dangerAlertEmailRepository.findAll()) {
      emailService.sendEmailWithoutAuthorization(
          address.getEmail(), "Danger Alert for Student", out.toString());
    }
  }
}
