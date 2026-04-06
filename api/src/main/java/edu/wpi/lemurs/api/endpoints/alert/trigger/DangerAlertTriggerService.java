/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.trigger;

import edu.wpi.lemurs.api.endpoints.alert.danger.DangerAlertEmailService;
import edu.wpi.lemurs.api.endpoints.survey.answer.AnswerDto;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

/** This service is responsible for checking for conditions that should trigger a danger alert. */
@Service
public class DangerAlertTriggerService {

  private static final Logger logger = LogManager.getLogger(DangerAlertTriggerService.class);

  // Cache of active triggers for performance
  private Map<Integer, DangerAlertTrigger> activeTriggers = new HashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private final DangerAlertEmailService dangerAlertEmailService;
  private final DangerAlertTriggerRepository triggerRepository;

  @Autowired
  public DangerAlertTriggerService(
      DangerAlertEmailService dangerAlertEmailService,
      DangerAlertTriggerRepository triggerRepository) {
    this.dangerAlertEmailService = dangerAlertEmailService;
    this.triggerRepository = triggerRepository;
  }

  @PostConstruct
  public void init() {
    loadActiveTriggers();
    // Refresh triggers every hour
    scheduler.scheduleAtFixedRate(this::loadActiveTriggers, 1, 60, TimeUnit.MINUTES);
  }

  private synchronized void loadActiveTriggers() {
    try {
      logger.info("Loading active danger alert triggers from database");
      Map<Integer, DangerAlertTrigger> newTriggers = new HashMap<>();
      List<DangerAlertTrigger> triggers = triggerRepository.findAllActive();

      for (DangerAlertTrigger trigger : triggers) {
        newTriggers.put(trigger.getQuestionId(), trigger);
      }

      this.activeTriggers = newTriggers;
      logger.info("Loaded {} active danger alert triggers", newTriggers.size());
    } catch (Exception e) {
      logger.error("Failed to load danger alert triggers", e);
    }
  }

  public void checkAnswersForDangerAlerts(Integer userId, List<AnswerDto> answers) {
    List<String> dangerReasons = new ArrayList<>();
    boolean sendEmail = false;

    for (AnswerDto answerDto : answers) {
      DangerAlertTrigger trigger = activeTriggers.get(answerDto.getQuestionId());

      if (trigger != null) {
        String answer = answerDto.getAnswer();
        int score;
        try {
          if ("yes".equalsIgnoreCase(answer)) {
            score = 1;
          } else if ("no".equalsIgnoreCase(answer)) {
            score = 0;
          } else {
            score = Integer.parseInt(answer);
          }
          if (score >= trigger.getThreshold()-1) {
              sendEmail = sendEmail || trigger.getSendEmail();
            String message = trigger.getAlertMessage().replace("{score}", String.valueOf(score));
            dangerReasons.add(message);
          }
        } catch (NumberFormatException e) {
          logger.warn(
              "Could not parse answer for question id {} for user id {}. Answer was: {}",
              answerDto.getQuestionId(),
              userId,
              answer);
        }
      }
    }

    if (!dangerReasons.isEmpty() && sendEmail) {
      try {
        dangerAlertEmailService.sendAlertWithoutAuthCheck(userId, dangerReasons);
      } catch (MessagingException
          | UnsupportedEncodingException
          | MailException
          | EntityDoesNotExistException e) {
        logger.error("Failed to send danger alert email for user {}", userId, e);
      }
    }
  }

  public void refreshTriggers() {
    loadActiveTriggers();
  }

  // Expose active triggers to other services (read-only usage)
  public Map<Integer, DangerAlertTrigger> getActiveTriggersMap() {
    return activeTriggers;
  }
}
