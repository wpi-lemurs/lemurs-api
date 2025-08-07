/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.trigger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DangerAlertTriggerRepository extends JpaRepository<DangerAlertTrigger, Integer> {

  @Query("SELECT t FROM DangerAlertTrigger t WHERE t.isActive = true")
  List<DangerAlertTrigger> findAllActive();
}
