/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.rule;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller for managing {@link AlertRule}s. */
@RestController
public class AlertRuleController {

  private final AlertRuleService alertRuleService;

  public AlertRuleController(AlertRuleService alertRuleService) {
    this.alertRuleService = alertRuleService;
  }

  /** The {@code GET /alert/rules} endpoint retrieves all configured alert rules. */
  @GetMapping("/alert/rules")
  public ResponseEntity<List<AlertRule>> getRules() {
    try {
      return new ResponseEntity<>(alertRuleService.getAllRules(), HttpStatus.OK);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /** The {@code POST /alert/rules} endpoint creates a new alert rule. */
  @PostMapping("/alert/rules")
  public ResponseEntity<AlertRule> createRule(@RequestBody AlertRuleDto ruleDto) {
    try {
      AlertRule createdRule = alertRuleService.createRule(ruleDto);
      return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /** The {@code DELETE /alert/rules/{id}} endpoint deletes an alert rule by its ID. */
  @DeleteMapping("/alert/rules/{id}")
  public ResponseEntity<Void> deleteRule(@PathVariable Integer id) {
    try {
      alertRuleService.deleteRule(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (UnauthenticatedException e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (UnauthorizedException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}