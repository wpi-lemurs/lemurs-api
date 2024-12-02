/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.progress;

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

  private static Integer GOAL_2_WEEKS = 0;
  private static Integer GOAL_3_WEEKS = 1;
  private static Integer GOAL_TOTAL = 2;

  private SecurityService securityService;
  private ProgressRepository progressRepository;
  private GoalRepository goalRepository;
  private GoalProgressRepository goalProgressRepository;
  private IncentiveRepository incentiveRepository;

  @Autowired
  public ProgressService(
      SecurityService securityService,
      ProgressRepository progressRepository,
      GoalRepository goalRepository,
      GoalProgressRepository goalProgressRepository,
      IncentiveRepository incentiveRepository) {
    this.securityService = securityService;
    this.progressRepository = progressRepository;
    this.goalRepository = goalRepository;
    this.goalProgressRepository = goalProgressRepository;
    this.incentiveRepository = incentiveRepository;
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
   * Starts goal progress for a user that doesn't already have progress.
   *
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public List<GoalProgress> getGoalProgress()
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    List<GoalProgress> goals = new ArrayList<>();
    for (GoalProgress goal :
        goalProgressRepository.findByUserID(securityService.getUser().getId())) {
      goals.add(goal);
    }
    if (!goals.isEmpty()) {
      return goals;
    }

    Date now = new Date();
    Integer userID = securityService.getUser().getId();

    for (Goal goal : goalRepository.findAll()) {
      goals.add(
          new GoalProgress(
              userID,
              goal.getId(),
              false,
              new Date(now.getTime() + 1000 * 60 * 60 * goal.getMaxDays())));
    }
    goals = new ArrayList<>();
    for (GoalProgress goal : goalProgressRepository.saveAll(goals)) {
      goals.add(goal);
    }
    return goals;
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

    // TODO: Make this much better, preferably after a discussion on api contract.
    Integer weeklyGoalID = GOAL_3_WEEKS;
    for (GoalProgress goalProgress : goalProgresses) {
      if (goalProgress.getGoalID().equals(GOAL_2_WEEKS) && goalProgress.getTimeLimit().after(now)) {
        weeklyGoalID = GOAL_2_WEEKS;
        break;
      }
    }

    Goal weeklyGoal = goalRepository.findById(weeklyGoalID).get();
    Goal totalGoal = goalRepository.findById(GOAL_TOTAL).get();

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
    AvailableResponse daily = new AvailableResponse("daily", progress.getNextDailySurvey());
    AvailableResponse weekly = new AvailableResponse("weekly", progress.getNextWeeklySurvey());
    availability.add(daily);
    availability.add(weekly);
    return availability;
  }
}
