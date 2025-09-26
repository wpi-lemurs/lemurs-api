/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Repository for managing WritingResponse entities. */
@Repository
public interface WrittenResponseRepository extends CrudRepository<WrittenResponse, Integer> {
  // can add your own custom query methods here:
}
