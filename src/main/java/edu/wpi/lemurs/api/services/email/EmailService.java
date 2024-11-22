/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.services.email;

import edu.wpi.lemurs.api.services.EnvironmentService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * The {@link EmailService} is a service that provides the ability to send emails as the
 * application.
 */
@Service
public class EmailService {

  private static final String EMAIL_DISPLAY_NAME = "LEMURS TEAM";

  private EnvironmentService env;
  private JavaMailSender emailSender;

  /** Autowires a {@link EmailService}. */
  @Autowired
  public EmailService(EnvironmentService environmentService, JavaMailSender emailSender) {
    this.env = environmentService;
    this.emailSender = emailSender;
  }

  /**
   * Sends an email as the application and skips authentication/authorization checks.
   *
   * @param to The email address of the recipient.
   * @param subject The subject line of the email.
   * @param text The text of the email.
   * @throws MessagingException Thrown if the email content creation fails.
   * @throws UnsupportedEncodingException Thrown if the email creation for the sender fails.
   * @throws MailException Thrown if the email failed to send.
   * @apiNote This method does not check for authorization.
   */
  public void sendEmailWithoutAuthorization(String to, String subject, String text)
      throws MessagingException, UnsupportedEncodingException, MailException {
    MimeMessage message = emailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setFrom(new InternetAddress(env.getLemursEmailAddress(), EMAIL_DISPLAY_NAME));
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(text);

    emailSender.send(message);
  }
}
