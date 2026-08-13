/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.data.availability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.wpi.lemurs.api.TestConstants;
import edu.wpi.lemurs.api.endpoints.progress.Progress;
import edu.wpi.lemurs.api.endpoints.progress.ProgressRepository;
import edu.wpi.lemurs.api.endpoints.survey.SurveyStatusResponse;
import edu.wpi.lemurs.api.endpoints.survey.SurveyWindowDto;
import edu.wpi.lemurs.api.endpoints.survey.answer.SurveyResponse;
import edu.wpi.lemurs.api.endpoints.survey.answer.SurveyResponseRepository;
import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.security.SecurityService;
import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Tests the {@link SurveyAvailabilityService}. */
class SurveyAvailabilityServiceTest implements TestConstants {

  private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
  private static final ZoneId LOS_ANGELES = ZoneId.of("America/Los_Angeles");
  private static final ZoneId KOLKATA = ZoneId.of("Asia/Kolkata");

  private static final Integer MORNING_SURVEY_ID = 0;
  private static final Integer AFTERNOON_SURVEY_ID = 1;

  private SecurityService securityService;
  private SurveyAvailabilityRepository surveyAvailabilityRepository;
  private SurveyResponseRepository surveyResponseRepository;
  private ProgressRepository progressRepository;
  private SurveyAvailabilityService service;

  /** Submissions the fake repository will serve, filtered by the requested instant range. */
  private List<SurveyResponse> storedResponses;

  @BeforeEach
  void setup() throws Exception {
    securityService = mock(SecurityService.class);
    surveyAvailabilityRepository = mock(SurveyAvailabilityRepository.class);
    surveyResponseRepository = mock(SurveyResponseRepository.class);
    progressRepository = mock(ProgressRepository.class);
    when(progressRepository.findById(TEST_ID_0)).thenReturn(Optional.empty());

    SurveyWindowProperties properties = new SurveyWindowProperties();
    properties.setWindowSurveyIds(
        Map.of("morning", MORNING_SURVEY_ID, "afternoon", AFTERNOON_SURVEY_ID));

    service =
        new SurveyAvailabilityService(
            securityService,
            surveyAvailabilityRepository,
            surveyResponseRepository,
            progressRepository,
            properties);

    when(securityService.getUser()).thenReturn(new User(TEST_ID_0, false, false));
    when(surveyAvailabilityRepository.findAll())
        .thenReturn(
            List.of(
                availability("morning", LocalTime.of(8, 0), LocalTime.of(13, 0)),
                availability("afternoon", LocalTime.of(15, 0), LocalTime.of(20, 0))));

    // Behave like the real query: return only the submissions inside [from, to).
    storedResponses = new ArrayList<>();
    when(surveyResponseRepository.findByUserBetween(eq(TEST_ID_0), any(), any()))
        .thenAnswer(
            invocation -> {
              Date from = invocation.getArgument(1);
              Date to = invocation.getArgument(2);
              List<SurveyResponse> matching = new ArrayList<>();
              for (SurveyResponse response : storedResponses) {
                if (!response.getTimestamp().before(from) && response.getTimestamp().before(to)) {
                  matching.add(response);
                }
              }
              return matching;
            });
  }

  private SurveyAvailability availability(String name, LocalTime open, LocalTime close) {
    return new SurveyAvailability(name, Time.valueOf(open), Time.valueOf(close));
  }

  /** Records a submission of the given survey at the given instant. */
  private void submitted(Integer surveyId, Instant when) {
    storedResponses.add(
        new SurveyResponse(1, TEST_ID_0, surveyId, Date.from(when), Date.from(when), null));
  }

  /** The instant at which it is the given wall-clock time in the given zone. */
  private Instant localTime(ZoneId zone, int year, int month, int day, int hour, int minute) {
    return LocalDate.of(year, month, day).atTime(hour, minute).atZone(zone).toInstant();
  }

  // --- window definitions ---------------------------------------------------

  /** Tests that the configured windows are reported with their wall-clock times. */
  @Test
  void testReportsWindowDefinitions() throws Exception {
    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 30), NEW_YORK);

    assertThat(status.getWindows())
        .extracting(SurveyWindowDto::getName, SurveyWindowDto::getSurveyId)
        .containsExactly(
            org.assertj.core.groups.Tuple.tuple("morning", MORNING_SURVEY_ID),
            org.assertj.core.groups.Tuple.tuple("afternoon", AFTERNOON_SURVEY_ID));

    assertThat(status.getWindows().get(0).getOpenTime()).isEqualTo(LocalTime.of(8, 0));
    assertThat(status.getWindows().get(0).getCloseTime()).isEqualTo(LocalTime.of(13, 0));
  }

  /** Tests that a participant with no submissions has completed nothing. */
  @Test
  void testNoSubmissionsMeansNothingCompleted() throws Exception {
    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 30), NEW_YORK);

    assertThat(status.getCompletedWindows()).isEmpty();
  }

  // --- completion is scoped to the participant's own local day --------------

  /** Tests that a morning submission marks only the morning window complete. */
  @Test
  void testMorningSubmissionCompletesOnlyMorning() throws Exception {
    submitted(MORNING_SURVEY_ID, localTime(NEW_YORK, 2026, 7, 30, 9, 0));

    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 30), NEW_YORK);

    assertThat(status.getCompletedWindows()).containsExactly("morning");
  }

  /** Tests that both windows can be completed within one local day. */
  @Test
  void testBothWindowsCanComplete() throws Exception {
    submitted(MORNING_SURVEY_ID, localTime(NEW_YORK, 2026, 7, 30, 9, 0));
    submitted(AFTERNOON_SURVEY_ID, localTime(NEW_YORK, 2026, 7, 30, 16, 0));

    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 30), NEW_YORK);

    assertThat(status.getCompletedWindows()).containsExactly("morning", "afternoon");
  }

  /**
   * Tests the case this work exists for.
   *
   * <p>A participant in Kolkata submits at 09:00 their time on 30 July. That same instant is 23:30
   * on 29 July in New York. The submission must count against the participant's own 30 July, not
   * against the server's calendar.
   */
  @Test
  void testSubmissionCountsAgainstTheParticipantsLocalDay() throws Exception {
    Instant kolkataMorning = localTime(KOLKATA, 2026, 7, 30, 9, 0);
    submitted(MORNING_SURVEY_ID, kolkataMorning);

    assertThat(service.getStatus(LocalDate.of(2026, 7, 30), KOLKATA).getCompletedWindows())
        .containsExactly("morning");

    // The same instant belongs to the previous day in New York.
    assertThat(service.getStatus(LocalDate.of(2026, 7, 30), NEW_YORK).getCompletedWindows())
        .isEmpty();
    assertThat(service.getStatus(LocalDate.of(2026, 7, 29), NEW_YORK).getCompletedWindows())
        .containsExactly("morning");
  }

  /** Tests that yesterday's submission does not count as today's. */
  @Test
  void testPreviousDaySubmissionDoesNotCount() throws Exception {
    submitted(MORNING_SURVEY_ID, localTime(NEW_YORK, 2026, 7, 29, 9, 0));

    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 30), NEW_YORK);

    assertThat(status.getCompletedWindows()).isEmpty();
  }

  /** Tests that a submission one second before local midnight still counts for that day. */
  @Test
  void testSubmissionJustBeforeLocalMidnightCounts() throws Exception {
    submitted(MORNING_SURVEY_ID, localTime(NEW_YORK, 2026, 7, 30, 23, 59).plusSeconds(59));

    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 30), NEW_YORK);

    assertThat(status.getCompletedWindows()).containsExactly("morning");
  }

  // --- the query range itself -----------------------------------------------

  /** Tests that the queried range is the participant's local day, not the server's. */
  @Test
  void testQueriesTheParticipantsLocalDayBoundaries() throws Exception {
    service.getStatus(LocalDate.of(2026, 7, 30), LOS_ANGELES);

    ArgumentCaptor<Date> from = ArgumentCaptor.forClass(Date.class);
    ArgumentCaptor<Date> to = ArgumentCaptor.forClass(Date.class);
    verify(surveyResponseRepository).findByUserBetween(eq(TEST_ID_0), from.capture(), to.capture());

    // 2026-07-30T00:00 in Los Angeles is 07:00Z; the day ends 24 hours later.
    assertThat(from.getValue().toInstant()).isEqualTo(Instant.parse("2026-07-30T07:00:00Z"));
    assertThat(to.getValue().toInstant()).isEqualTo(Instant.parse("2026-07-31T07:00:00Z"));
  }

  /** Tests that a local day shortened by a daylight saving transition is only 23 hours long. */
  @Test
  void testSpringForwardDayIsTwentyThreeHours() throws Exception {
    // US daylight saving begins Sunday 8 March 2026.
    service.getStatus(LocalDate.of(2026, 3, 8), NEW_YORK);

    ArgumentCaptor<Date> from = ArgumentCaptor.forClass(Date.class);
    ArgumentCaptor<Date> to = ArgumentCaptor.forClass(Date.class);
    verify(surveyResponseRepository).findByUserBetween(eq(TEST_ID_0), from.capture(), to.capture());

    Duration length = Duration.between(from.getValue().toInstant(), to.getValue().toInstant());
    assertThat(length).isEqualTo(Duration.ofHours(23));
  }

  /** Tests that a local day lengthened by a daylight saving transition is 25 hours long. */
  @Test
  void testFallBackDayIsTwentyFiveHours() throws Exception {
    // US daylight saving ends Sunday 1 November 2026.
    service.getStatus(LocalDate.of(2026, 11, 1), NEW_YORK);

    ArgumentCaptor<Date> from = ArgumentCaptor.forClass(Date.class);
    ArgumentCaptor<Date> to = ArgumentCaptor.forClass(Date.class);
    verify(surveyResponseRepository).findByUserBetween(eq(TEST_ID_0), from.capture(), to.capture());

    Duration length = Duration.between(from.getValue().toInstant(), to.getValue().toInstant());
    assertThat(length).isEqualTo(Duration.ofHours(25));
  }

  // --- weekly survey --------------------------------------------------------

  /** Tests that a participant with no progress row yet has the weekly survey open. */
  @Test
  void testWeeklyIsOpenWithoutProgress() throws Exception {
    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 30), NEW_YORK);

    assertThat(status.getWeeklyNextAvailable()).isNull();
  }

  /** Tests that the weekly next-available instant is reported verbatim from progress. */
  @Test
  void testWeeklyNextAvailableComesFromProgress() throws Exception {
    Instant nextWeekly = Instant.parse("2026-08-06T17:00:00Z");
    Progress progress =
        new Progress(
            TEST_ID_0,
            java.math.BigDecimal.ZERO,
            0,
            0,
            new Date(),
            new Date(),
            Date.from(nextWeekly));
    when(progressRepository.findById(TEST_ID_0)).thenReturn(Optional.of(progress));

    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 30), KOLKATA);

    assertThat(status.getWeeklyNextAvailable().toInstant()).isEqualTo(nextWeekly);
  }

  // --- configuration --------------------------------------------------------

  /** Tests that a window with no configured survey id is reported but never marked complete. */
  @Test
  void testUnmappedWindowIsNeverComplete() throws Exception {
    SurveyWindowProperties empty = new SurveyWindowProperties();
    service =
        new SurveyAvailabilityService(
            securityService,
            surveyAvailabilityRepository,
            surveyResponseRepository,
            progressRepository,
            empty);
    submitted(MORNING_SURVEY_ID, localTime(NEW_YORK, 2026, 7, 30, 9, 0));

    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 30), NEW_YORK);

    assertThat(status.getWindows()).hasSize(2);
    assertThat(status.getCompletedWindows()).isEmpty();
  }

  @Test
  void testStudyNotConcludedOnDayTwentyEight() throws Exception {
    LocalDate startedDate = LocalDate.of(2026, 7, 1);
    Instant startedInstant = startedDate.atStartOfDay(NEW_YORK).toInstant();
    Progress progress =
        new Progress(
            TEST_ID_0,
            java.math.BigDecimal.ZERO,
            1,
            0,
            Date.from(startedInstant),
            new Date(),
            new Date());
    when(progressRepository.findById(TEST_ID_0)).thenReturn(Optional.of(progress));

    // July 29 is 28 days after July 1
    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 29), NEW_YORK);
    assertThat(status.getStudyConcluded()).isFalse();
    assertThat(status.getWindows()).hasSize(2);
  }

  @Test
  void testStudyConcludedAfterTwentyEightFullDays() throws Exception {
    LocalDate startedDate = LocalDate.of(2026, 7, 1);
    Instant startedInstant = startedDate.atStartOfDay(NEW_YORK).toInstant();
    Progress progress =
        new Progress(
            TEST_ID_0,
            java.math.BigDecimal.ZERO,
            1,
            0,
            Date.from(startedInstant),
            new Date(),
            new Date());
    when(progressRepository.findById(TEST_ID_0)).thenReturn(Optional.of(progress));

    // July 30 is 29 days after July 1 (> 28 days elapsed)
    SurveyStatusResponse status = service.getStatus(LocalDate.of(2026, 7, 30), NEW_YORK);
    assertThat(status.getStudyConcluded()).isTrue();
    assertThat(status.getWindows()).isEmpty();
  }
}
