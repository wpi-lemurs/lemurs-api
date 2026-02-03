/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import edu.wpi.lemurs.api.endpoints.progress.ProgressService;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
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
public class AudioService {

  private static final Logger logger = LoggerFactory.getLogger(AudioService.class);

  private final SecurityService securityService;
  private final AudioResponseRepository audioResponseRepository;
  private final ProgressService progressService;

  @Autowired
  public AudioService(
      AudioResponseRepository audioResponseRepository,
      SecurityService securityService,
      ProgressService progressService) {
    this.audioResponseRepository = audioResponseRepository;
    this.securityService = securityService;
    this.progressService = progressService;
  }

  /**
   * Saves an audio response to the database. The audio data is expected to be Base64 encoded and
   * will be decoded before storage.
   *
   * @param audioDataRequest The audio data request containing all necessary fields
   * @throws UnauthenticatedException Thrown if the user is not authenticated
   * @throws UnauthorizedException Thrown if the user does not have USER permissions
   * @throws IllegalArgumentException Thrown if the Base64 data is invalid
   */
  public void saveAudioData(AudioDataRequest audioDataRequest)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    Integer userId = securityService.getUser().getId();

    logger.info(
        "Saving audio data for user {} - survey response {} question {} at {}",
        userId,
        audioDataRequest.getSurveyResponseId(),
        audioDataRequest.getAudioQuestionId(),
        audioDataRequest.getTimestamp());

    try {
      // Decode Base64 audio data
      byte[] audioData = Base64.getDecoder().decode(audioDataRequest.getAudioByte64());

      logger.debug(
          "Decoded audio data: {} bytes for user {} survey response {}",
          audioData.length,
          userId,
          audioDataRequest.getSurveyResponseId());

      // Create and save the audio response
      AudioResponse audioResponse = new AudioResponse();
      audioResponse.setSurveyResponseId(audioDataRequest.getSurveyResponseId());
      audioResponse.setAudioQuestionId(audioDataRequest.getAudioQuestionId());
      audioResponse.setAudioData(audioData);
      audioResponse.setTimestamp(audioDataRequest.getTimestamp());

      AudioResponse savedResponse = audioResponseRepository.save(audioResponse);

      // Add audio response bonus to user's earnings
      progressService.recordAudioBonus();

      logger.info(
          "Successfully saved audio response {} for user {} - {} bytes",
          savedResponse.getId(),
          userId,
          audioData.length);
    } catch (IllegalArgumentException e) {
      logger.error(
          "Failed to decode Base64 audio data for user {} survey response {}: {}",
          userId,
          audioDataRequest.getSurveyResponseId(),
          e.getMessage());
      throw new RuntimeException("Invalid Base64 audio data", e);
    }
  }

  /** Gets all audio responses for a specific survey response. */
  public List<AudioResponse> getAudioResponsesBySurveyResponse(Integer surveyResponseId)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.RESEARCHER);
    return audioResponseRepository.findBySurveyResponseIdOrderByTimestampDesc(surveyResponseId);
  }

  /** Gets a specific audio response by ID. */
  public AudioResponse getAudioResponse(Integer id)
      throws EntityDoesNotExistException, UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.RESEARCHER);

    Optional<AudioResponse> audioResponse = audioResponseRepository.findById(id);

    if (audioResponse.isEmpty()) {
      throw new EntityDoesNotExistException();
    }

    return audioResponse.get();
  }
}
