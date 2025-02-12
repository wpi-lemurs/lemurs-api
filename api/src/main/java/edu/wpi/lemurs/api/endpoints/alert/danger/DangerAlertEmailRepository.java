/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.danger;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link DangerAlertEmailRepository}. */
public interface DangerAlertEmailRepository extends CrudRepository<DangerAlertEmail, String> {}
