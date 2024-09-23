/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.data;

import org.springframework.data.repository.CrudRepository;

/** A {@link CrudRepository} for a {@link DataRepository}. */
public interface DataRepository extends CrudRepository<Data, Integer> {}
