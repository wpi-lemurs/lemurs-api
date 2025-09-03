/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling audio responses from weekly surveys. This endpoint receives audio data
 * linked to a specific survey response ID.
 */
@RestController
public class AudioController {

  private static final Logger logger = LoggerFactory.getLogger(AudioController.class);

  private final AudioService audioService;

  @Autowired
  public AudioController(AudioService audioService) {
    this.audioService = audioService;
  }

  /**
   * The <code>/data/audio</code> {@code POST} endpoint saves audio responses. This endpoint is
   * called by the client as part of the weekly survey submission process. The audio data is
   * expected to be Base64 encoded and will be decoded before storage.
   */
  @PostMapping("/data/audio")
  public ResponseEntity<Void> saveAudioData(@RequestBody AudioDataRequest request) {
    try {
      logger.info(
          "Received audio data request - survey response {} question {} at {}",
          request.getSurveyResponseId(),
          request.getAudioQuestionId(),
          request.getTimestamp());

      // Validate request data
      if (request.getTimestamp() == null) {
        logger.warn("Audio request missing timestamp");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      if (request.getAudioQuestionId() == null) {
        logger.warn("Audio request missing audioQuestionId");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      if (request.getSurveyResponseId() == null) {
        logger.warn("Audio request missing surveyResponseId");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      if (request.getAudioByte64() == null || request.getAudioByte64().trim().isEmpty()) {
        logger.warn("Audio request missing audio data");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      // Basic validation of Base64 format
      String audioData = request.getAudioByte64().trim();
      if (audioData.length() % 4 != 0) {
        logger.warn("Audio data appears to be invalid Base64 format");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      // Save the audio response
      audioService.saveAudioData(request);

      logger.info(
          "Successfully saved audio data for survey response {} - question {} ({} chars Base64)",
          request.getSurveyResponseId(),
          request.getAudioQuestionId(),
          audioData.length());

      return new ResponseEntity<>(HttpStatus.CREATED);

    } catch (UnauthenticatedException e) {
      logger.warn("Unauthenticated audio data request");
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      logger.warn("Unauthorized audio data request");
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid audio data request (likely bad Base64): {}", e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } catch (RuntimeException e) {
      if (e.getMessage().contains("Base64")) {
        logger.error("Base64 decoding error for audio data: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      logger.error("Runtime error saving audio data", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error saving audio data", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
