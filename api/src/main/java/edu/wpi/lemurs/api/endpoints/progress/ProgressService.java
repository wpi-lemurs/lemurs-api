/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.progress;

import edu.wpi.lemurs.api.data.availability.SurveyAvailabilityService;
import edu.wpi.lemurs.api.endpoints.data.AudioResponseRepository;
import edu.wpi.lemurs.api.endpoints.survey.answer.WrittenResponseRepository;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** A service for getting a user's progress. */
@Service
@Transactional
public class ProgressService {

  private static final Integer GOAL_2_WEEKS = 0;
  private static final Integer GOAL_3_WEEKS = 1;
  private static final Integer GOAL_TOTAL = 2;

  private static final Integer DAILY_INCENTIVE_ID = 0;
  private static final Integer WEEKLY_BASE_INCENTIVE_ID = 1;
  private static final Integer WEEKLY_AUDIO_BONUS_ID = 2;
  private static final Integer WEEKLY_WRITTEN_BONUS_ID = 3;

  private SecurityService securityService;
  private ProgressRepository progressRepository;
  private GoalRepository goalRepository;
  private GoalProgressRepository goalProgressRepository;
  private IncentiveRepository incentiveRepository;
  private SurveyAvailabilityService surveyAvailabilityService;
  private AudioResponseRepository audioResponseRepository;
  private WrittenResponseRepository writtenResponseRepository;

  @Autowired
  public ProgressService(
      SecurityService securityService,
      ProgressRepository progressRepository,
      GoalRepository goalRepository,
      GoalProgressRepository goalProgressRepository,
      IncentiveRepository incentiveRepository,
      SurveyAvailabilityService surveyAvailabilityService,
      AudioResponseRepository audioResponseRepository,
      WrittenResponseRepository writtenResponseRepository) {
    this.securityService = securityService;
    this.progressRepository = progressRepository;
    this.goalRepository = goalRepository;
    this.goalProgressRepository = goalProgressRepository;
    this.incentiveRepository = incentiveRepository;
    this.surveyAvailabilityService = surveyAvailabilityService;
    this.audioResponseRepository = audioResponseRepository;
    this.writtenResponseRepository = writtenResponseRepository;
  }

  /**
   * Gets progress for a user that may or may not already have progress.
   *
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public Progress getProgress() throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    Optional<Progress> found = progressRepository.findById(securityService.getUser().getId());

    if (found.isPresent()) {
      return found.get();
    }

    Date now = new Date();

    Progress progress =
        new Progress(securityService.getUser().getId(), new BigDecimal(0), 0, 0, now, now, now);

    return progressRepository.save(progress);
  }

  /**
   * Gets goal progress for a user, creating initial progress if none exists.
   *
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public List<GoalProgress> getGoalProgress()
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    List<GoalProgress> existingGoals = new ArrayList<>();
    goalProgressRepository
        .findByUserID(securityService.getUser().getId())
        .forEach(existingGoals::add);

    if (!existingGoals.isEmpty()) {
      return existingGoals;
    }

    Date now = new Date();
    Integer userID = securityService.getUser().getId();
    List<GoalProgress> newGoals = new ArrayList<>();

    for (Goal goal : goalRepository.findAll()) {
      newGoals.add(
          new GoalProgress(
              userID,
              goal.getId(),
              false,
              new Date(now.getTime() + 1000L * 60 * 60 * 24 * goal.getMaxDays())));
    }

    List<GoalProgress> savedGoals = new ArrayList<>();
    goalProgressRepository.saveAll(newGoals).forEach(savedGoals::add);
    return savedGoals;
  }

  /**
   * Gets the progress of a user.
   *
   * @return The user's progress.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public ProgressResponse getProgressResponse()
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    Progress progress = getProgress();
    List<GoalProgress> goalProgresses = getGoalProgress();

    Date now = new Date();

    // Determine the appropriate weekly goal to display
    Integer weeklyGoalID = determineActiveWeeklyGoal(goalProgresses, now);

    // Get goals with proper error handling
    Optional<Goal> weeklyGoalOpt = goalRepository.findById(weeklyGoalID);
    Optional<Goal> totalGoalOpt = goalRepository.findById(GOAL_TOTAL);

    if (weeklyGoalOpt.isEmpty()) {
      throw new IllegalStateException("Weekly goal with ID " + weeklyGoalID + " not found");
    }
    if (totalGoalOpt.isEmpty()) {
      throw new IllegalStateException("Total goal with ID " + GOAL_TOTAL + " not found");
    }

    Goal weeklyGoal = weeklyGoalOpt.get();
    Goal totalGoal = totalGoalOpt.get();

    return new ProgressResponse(
        progress.getEarned(),
        progress.getDailySurveysCompleted(),
        totalGoal.getRequiredDailySurveys(),
        totalGoal.getReward(),
        progress.getDailySurveysCompleted(),
        weeklyGoal.getRequiredDailySurveys(),
        weeklyGoal.getReward());
  }

  /**
   * Determines the active weekly goal based on goal progress and completion status. Prioritizes the
   * 2-week goal if it's not completed and still within time limit, otherwise falls back to the
   * 3-week goal.
   *
   * @param goalProgresses List of user's goal progress
   * @param currentTime Current timestamp for comparison
   * @return The ID of the active weekly goal
   */
  private Integer determineActiveWeeklyGoal(List<GoalProgress> goalProgresses, Date currentTime) {
    // First, try to find an active 2-week goal
    for (GoalProgress goalProgress : goalProgresses) {
      if (goalProgress.getGoalID().equals(GOAL_2_WEEKS)) {
        // Check if goal is not completed and still within time limit
        if (!goalProgress.isComplete()
            && goalProgress.getTimeLimit() != null
            && goalProgress.getTimeLimit().after(currentTime)) {
          return GOAL_2_WEEKS;
        }
        break; // We found the 2-week goal, no need to continue looking for it
      }
    }

    // If 2-week goal is completed or expired, check 3-week goal
    for (GoalProgress goalProgress : goalProgresses) {
      if (goalProgress.getGoalID().equals(GOAL_3_WEEKS)) {
        // Check if goal is not completed and still within time limit
        if (!goalProgress.isComplete()
            && goalProgress.getTimeLimit() != null
            && goalProgress.getTimeLimit().after(currentTime)) {
          return GOAL_3_WEEKS;
        }
        break;
      }
    }

    // Default to 3-week goal if no active goals found
    return GOAL_3_WEEKS;
  }

  /**
   * Gets the date for the next available daily and weekly survey.
   *
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public List<AvailableResponse> getSurveyAvailability()
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    Progress progress = getProgress();

    List<AvailableResponse> availability = new ArrayList<>();
    AvailableResponse daily =
        new AvailableResponse(
            "daily",
            surveyAvailabilityService.getNextAvailableSurveyOpen(progress.getNextDailySurvey()));
    AvailableResponse weekly = new AvailableResponse("weekly", progress.getNextWeeklySurvey());
    availability.add(daily);
    availability.add(weekly);
    return availability;
  }

  public void recordDaily(Date timestamp) throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    Progress progress = getProgress();
    Date now = new Date();
    // TODO: Determine how much to trust the user's timestamp.
    if (progress.getNextDailySurvey().after(now)) {
      return;
    }

    Optional<Incentive> incentiveOpt = incentiveRepository.findById(DAILY_INCENTIVE_ID);
    if (incentiveOpt.isEmpty()) {
      throw new IllegalStateException(
          "Daily incentive with ID " + DAILY_INCENTIVE_ID + " not found");
    }

    Incentive incentive = incentiveOpt.get();
    BigDecimal totalEarned = progress.getEarned().add(incentive.getReward());

    // Update daily surveys completed count first
    int newDailySurveysCompleted = progress.getDailySurveysCompleted() + 1;

    // Check and complete goals based on the new count
    for (GoalProgress goalProgress : getGoalProgress()) {
      if (!goalProgress.isComplete()) {
        Optional<Goal> goalOpt = goalRepository.findById(goalProgress.getGoalID());
        if (goalOpt.isEmpty()) {
          // Log warning but continue processing other goals
          System.err.println("Warning: Goal with ID " + goalProgress.getGoalID() + " not found");
          continue;
        }

        Goal goal = goalOpt.get();
        if (goal.getRequiredDailySurveys() <= newDailySurveysCompleted) {
          goalProgress.setComplete(true);
          totalEarned = totalEarned.add(goal.getReward());
          goalProgressRepository.save(goalProgress); // Persist the completion status
        }
      }
    }

    progress.setNextDailySurvey(surveyAvailabilityService.getEndOfCurrentAvailableSurvey(now));
    progress.setDailySurveysCompleted(newDailySurveysCompleted);
    progress.setEarned(totalEarned);
    progressRepository.save(progress); // Ensure progress is persisted
  }

  /**
   * Records completion of a weekly survey and calculates reward based on completed components. Base
   * reward is given for PHQ-9, with bonuses for audio and written responses.
   *
   * @param timestamp The timestamp of survey completion.
   * @param surveyResponseId The survey response ID to check for audio/written responses.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public void recordWeekly(Date timestamp, Integer surveyResponseId)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    Progress progress = getProgress();
    Date now = new Date();
    // TODO: Determine how much to trust the user's timestamp.
    if (progress.getNextWeeklySurvey().after(now)) {
      return;
    }

    // Start with base PHQ-9 reward
    Optional<Incentive> baseIncentiveOpt = incentiveRepository.findById(WEEKLY_BASE_INCENTIVE_ID);
    if (baseIncentiveOpt.isEmpty()) {
      throw new IllegalStateException(
          "Weekly base incentive with ID " + WEEKLY_BASE_INCENTIVE_ID + " not found");
    }
    BigDecimal totalReward = baseIncentiveOpt.get().getReward();

    // Check if audio responses exist for this survey and add bonus
    if (audioResponseRepository.existsBySurveyResponseId(surveyResponseId)) {
      Optional<Incentive> audioIncentiveOpt = incentiveRepository.findById(WEEKLY_AUDIO_BONUS_ID);
      if (audioIncentiveOpt.isPresent()) {
        totalReward = totalReward.add(audioIncentiveOpt.get().getReward());
      }
    }

    // Check if written responses exist for this survey and add bonus
    if (writtenResponseRepository.existsBySurveyResponseId(surveyResponseId)) {
      Optional<Incentive> writtenIncentiveOpt =
          incentiveRepository.findById(WEEKLY_WRITTEN_BONUS_ID);
      if (writtenIncentiveOpt.isPresent()) {
        totalReward = totalReward.add(writtenIncentiveOpt.get().getReward());
      }
    }

    progress.setNextWeeklySurvey(
        new Date(now.getTime() + 1000L * 60 * 60 * 24 * 7)); // TODO: Improve the logic.
    progress.setWeeklySurveysCompleted(progress.getWeeklySurveysCompleted() + 1);
    progress.setEarned(progress.getEarned().add(totalReward));
    progressRepository.save(progress); // Ensure progress is persisted
  }
}
