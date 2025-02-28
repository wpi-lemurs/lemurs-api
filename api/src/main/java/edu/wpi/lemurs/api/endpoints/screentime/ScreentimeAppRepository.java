/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.screentime;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link ScreentimeAppRepository}. */
public interface ScreentimeAppRepository extends CrudRepository<ScreentimeApp, Integer> {}
