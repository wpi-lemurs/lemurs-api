/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.wpi.lemurs.api.TestConstants;
import edu.wpi.lemurs.api.endpoints.alert.trigger.DangerAlertTriggerService;
import edu.wpi.lemurs.api.endpoints.progress.ProgressService;
import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.security.SecurityService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests submission idempotency in {@link AnswerService}. */
class AnswerServiceTest implements TestConstants {

  private static final Integer SURVEY_ID = 0;
  private static final String SUBMISSION_ID = "1f0c3e5a-9b2d-4c77-8f31-0a5e6b7c8d9e";

  private SecurityService securityService;
  private AnswerRepository answerRepository;
  private SurveyResponseRepository surveyResponseRepository;
  private ProgressService progressService;
  private DangerAlertTriggerService dangerAlertTriggerService;
  private AnswerService answerService;

  /** Rows the repository is pretending to hold. */
  private List<SurveyResponse> stored;

  /** Ids handed out by the fake save(). */
  private int nextId;

  @BeforeEach
  void setUp() throws Exception {
    securityService = mock(SecurityService.class);
    answerRepository = mock(AnswerRepository.class);
    surveyResponseRepository = mock(SurveyResponseRepository.class);
    progressService = mock(ProgressService.class);
    dangerAlertTriggerService = mock(DangerAlertTriggerService.class);

    stored = new ArrayList<>();
    nextId = 1;

    User user = new User();
    user.setId(TEST_ID_0);
    when(securityService.getUser()).thenReturn(user);

    // save() assigns an id and remembers the row, so a second submission can
    // actually find the first.
    when(surveyResponseRepository.save(any(SurveyResponse.class)))
        .thenAnswer(
            invocation -> {
              SurveyResponse response = invocation.getArgument(0);
              response.setId(nextId++);
              stored.add(response);
              return response;
            });

    // Mirrors the unique index: scoped to the user.
    when(surveyResponseRepository.findByUserAndClientSubmissionId(anyInt(), anyString()))
        .thenAnswer(
            invocation -> {
              Integer userId = invocation.getArgument(0);
              String submissionId = invocation.getArgument(1);
              return stored.stream()
                  .filter(r -> userId.equals(r.getUserID()))
                  .filter(r -> submissionId.equals(r.getClientSubmissionId()))
                  .toList();
            });

    answerService =
        new AnswerService(
            securityService,
            answerRepository,
            surveyResponseRepository,
            progressService,
            dangerAlertTriggerService);
  }

  private CombinedSurveyResponseDto submission(String clientSubmissionId) {
    AnswerDto answer = new AnswerDto(1, "3");
    SurveyResponseDto survey = new SurveyResponseDto(SURVEY_ID, List.of(answer));
    return new CombinedSurveyResponseDto(
        new Date(), List.of(survey), new Date(), clientSubmissionId);
  }

  @Test
  void testASingleSubmissionIsStored() throws Exception {
    answerService.recordAnswersDaily(submission(SUBMISSION_ID));

    assertThat(stored).hasSize(1);
    assertThat(stored.get(0).getClientSubmissionId()).isEqualTo(SUBMISSION_ID);
  }

  @Test
  void testResubmittingTheSameAttemptStoresNothingNew() throws Exception {
    answerService.recordAnswersDaily(submission(SUBMISSION_ID));
    answerService.recordAnswersDaily(submission(SUBMISSION_ID));

    // The double-tap that produced eight rows for one participant in the live
    // study now produces one.
    assertThat(stored).hasSize(1);
  }

  @Test
  void testADuplicateDoesNotRecordProgressTwice() throws Exception {
    answerService.recordAnswersDaily(submission(SUBMISSION_ID));
    answerService.recordAnswersDaily(submission(SUBMISSION_ID));

    // Progress drives participant payment, so counting a survey twice would
    // overpay and inflate the completion figures.
    verify(progressService).recordDaily(any(Date.class));
  }

  @Test
  void testADuplicateDoesNotStoreAnswersTwice() throws Exception {
    answerService.recordAnswersDaily(submission(SUBMISSION_ID));
    answerService.recordAnswersDaily(submission(SUBMISSION_ID));

    verify(answerRepository).saveAll(any());
  }

  @Test
  void testADuplicateDoesNotReRunDangerAlertChecks() throws Exception {
    answerService.recordAnswersDaily(submission(SUBMISSION_ID));
    answerService.recordAnswersDaily(submission(SUBMISSION_ID));

    // Re-checking would email the study team a second time about one answer.
    verify(dangerAlertTriggerService).checkAnswersForDangerAlerts(eq(TEST_ID_0), any());
  }

  @Test
  void testADuplicateDoesNotRecordWeeklyProgressTwice() throws Exception {
    answerService.recordAnswersWeekly(submission(SUBMISSION_ID));
    answerService.recordAnswersWeekly(submission(SUBMISSION_ID));

    // The weekly bonus is worth $10, so counting it twice overpays.
    verify(progressService).recordWeekly(any(Date.class), anyInt());
  }

  @Test
  void testDistinctAttemptsAreBothStored() throws Exception {
    answerService.recordAnswersDaily(submission(SUBMISSION_ID));
    answerService.recordAnswersDaily(submission("a-different-attempt"));

    // Two genuine submissions must not be collapsed into one.
    assertThat(stored).hasSize(2);
  }

  @Test
  void testASubmissionWithoutAnIdIsStillStored() throws Exception {
    // An older client that does not send the field keeps working, just without
    // duplicate protection.
    answerService.recordAnswersDaily(submission(null));
    answerService.recordAnswersDaily(submission(null));

    assertThat(stored).hasSize(2);
    verify(surveyResponseRepository, never())
        .findByUserAndClientSubmissionId(anyInt(), anyString());
  }

  @Test
  void testABlankIdIsTreatedAsAbsent() throws Exception {
    answerService.recordAnswersDaily(submission("   "));

    assertThat(stored).hasSize(1);
    verify(surveyResponseRepository, never())
        .findByUserAndClientSubmissionId(anyInt(), anyString());
  }

  @Test
  void testTheDuplicateReturnsTheOriginalIds() throws Exception {
    answerService.recordAnswersWeekly(submission(SUBMISSION_ID));
    Integer secondCall = answerService.recordAnswersWeekly(submission(SUBMISSION_ID));

    // The retry reports the id it stored the first time, so the client can link
    // audio and written responses to the right row.
    assertThat(secondCall).isEqualTo(stored.get(0).getId());
    assertThat(stored).hasSize(1);
  }
}
