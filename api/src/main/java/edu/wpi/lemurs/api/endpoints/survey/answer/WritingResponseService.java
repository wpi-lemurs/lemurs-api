/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing audio responses from weekly surveys. This service handles Base64 decoding
 * and storage of audio data linked to survey responses.
 */
@Service
@Transactional
public class WritingResponseService {

  private static final Logger logger = LoggerFactory.getLogger(WritingResponseService.class);

  private final SecurityService securityService;


  @Autowired
  public WritingResponseService(SecurityService securityService) {
    this.securityService = securityService;
  }

  public void saveWritingData(WritingResponseDto request)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    Integer userId = securityService.getUser().getId();

    logger.info(
        "Saving audio data for user {} - survey response {} question {} at {}",
        userId,
        request.getSurveyResponseId(),
        request.getWrittenQuestionId(),
        request.getTimestamp());

    try {

      // Create and save the writing response
      WritingResponse writingResponse = new WritingResponse();
      writingResponse.setSurvey_response_id(request.getSurveyResponseId());
      writingResponse.setWritten_question_id(request.getWrittenQuestionId());
      writingResponse.setData(request.getData());
      writingResponse.setTimestamp(
          request.getTimestamp() != null ? request.getTimestamp() : new Date());
      writingResponse.setNotificationStart(
          request.getNotificationStart() != null ? request.getNotificationStart() : new Date());
      writingResponse.setNotificationStart(new Date());

      WritingResponseRepository writingResponseRepository = null;
      WritingResponse savedResponse = writingResponseRepository.save(writingResponse);

      logger.info(
          "Successfully saved writing response {} for user {}", savedResponse.getId(), userId);
    } catch (Exception e) {
      logger.error(
          "Failed to save writing response for user {} survey response {}: {}",
          userId,
          request.getSurveyResponseId(),
          e.getMessage(),
          e);
      throw new RuntimeException("Error saving writing response", e);
    }
  }
}
