/* Copyright (C) 2025 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.survey.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for managing WritingResponse entities. */
@Repository
public interface WritingResponseRepository extends JpaRepository<WritingResponse, Integer> {
  // can add your own custom query methods here:
}
