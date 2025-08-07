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

  /**
   * Loads active triggers from the database into memory cache.
   * This improves performance by avoiding database lookups on every answer check.
   */
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

  /**
   * Checks a list of survey answers for any that trigger a danger alert. If any triggers are found,
   * it sends an alert.
   *
   * @param userId The ID of the user who submitted the answers.
   * @param answers The list of answers to check.
   */
  public void checkAnswersForDangerAlerts(Integer userId, List<AnswerDto> answers) {
    List<String> dangerReasons = new ArrayList<>();

    for (AnswerDto answerDto : answers) {
      DangerAlertTrigger trigger = activeTriggers.get(answerDto.getId());
      
      if (trigger != null) {
        try {
          int score = Integer.parseInt(answerDto.getAnswer());
          if (score >= trigger.getThreshold()) {
            // Replace {score} placeholder with actual score
            String message = trigger.getAlertMessage().replace("{score}", String.valueOf(score));
            dangerReasons.add(message);
          }
        } catch (NumberFormatException e) {
          logger.warn(
              "Could not parse answer for question id {} for user id {}. Answer was: {}",
              answerDto.getId(),
              userId,
              answerDto.getAnswer());
        }
      }
    }

    if (!dangerReasons.isEmpty()) {
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
  
  // For manual trigger refresh (can be called from an admin controller)
  public void refreshTriggers() {
    loadActiveTriggers();
  }
}
