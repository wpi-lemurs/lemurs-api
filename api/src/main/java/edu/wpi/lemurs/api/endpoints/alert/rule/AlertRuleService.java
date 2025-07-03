/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.rule;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/** Service for managing {@link AlertRule}s. */
@Service
@Transactional
public class AlertRuleService {

  private final AlertRuleRepository alertRuleRepository;
  private final SecurityService securityService;

  public AlertRuleService(AlertRuleRepository alertRuleRepository, SecurityService securityService) {
    this.alertRuleRepository = alertRuleRepository;
    this.securityService = securityService;
  }

  /**
   * Gets all alert rules.
   *
   * @return A list of all {@link AlertRule}s.
   */
  public List<AlertRule> getAllRules() throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.STAFF);
    List<AlertRule> rules = new ArrayList<>();
    alertRuleRepository.findAll().forEach(rules::add);
    return rules;
  }

  /** Creates a new alert rule. */
  public AlertRule createRule(AlertRuleDto ruleDto)
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.STAFF);
    AlertRule newRule =
        AlertRule.builder()
            .questionId(ruleDto.getQuestionId())
            .operator(ruleDto.getOperator())
            .threshold(ruleDto.getThreshold())
            .reasonTemplate(ruleDto.getReasonTemplate())
            .build();
    return alertRuleRepository.save(newRule);
  }

  /** Deletes an alert rule by its ID. */
  public void deleteRule(Integer id) throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasPermission(LemursRole.STAFF);
    alertRuleRepository.deleteById(id);
  }
}