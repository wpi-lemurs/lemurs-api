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
public class WrittenResponseService {

  private static final Logger logger = LoggerFactory.getLogger(WrittenResponseService.class);

  private final SecurityService securityService;
  private final WrittenResponseRepository writingResponseRepository;

  @Autowired
  public WrittenResponseService(
      SecurityService securityService, WrittenResponseRepository writingResponseRepository) {
    this.securityService = securityService;
    this.writingResponseRepository = writingResponseRepository;
  }

  public void saveWritingData(WrittenResponseDto request)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    Integer userId = securityService.getUser().getId();

    logger.info(
        "Saving written data for user {} - Survey Response: {} Question: {} Data: {} At: {}",
        userId,
        request.getSurvey_response_id(),
        request.getWritten_question_id(),
        request.getWritten_data(),
        request.getTimestamp());

    try {

      // Create and save the writing response
      WrittenResponse writingResponse = new WrittenResponse();
      writingResponse.setSurvey_response_id(request.getSurvey_response_id());
      writingResponse.setWritten_question_id(request.getWritten_question_id());
      writingResponse.setWritten_data(request.getWritten_data());
      writingResponse.setTimestamp(
          request.getTimestamp() != null ? request.getTimestamp() : new Date());

      WrittenResponse savedResponse = writingResponseRepository.save(writingResponse);

      logger.info(
          "Successfully saved writing response {} for user {}", savedResponse.getId(), userId);
    } catch (Exception e) {
      logger.error(
          "Failed to save writing response for user {} survey response {}: Error: {}",
          userId,
          request.getSurvey_response_id(),
          e.getMessage(),
          e);
      throw new RuntimeException("Error saving writing response", e);
    }
  }
}
