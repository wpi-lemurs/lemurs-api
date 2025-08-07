/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.alert.trigger;

import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DangerAlertTriggerManagementService {

  private final DangerAlertTriggerRepository triggerRepository;
  private final DangerAlertTriggerService triggerService;
  private final SecurityService securityService;

  @Autowired
  public DangerAlertTriggerManagementService(
      DangerAlertTriggerRepository triggerRepository,
      DangerAlertTriggerService triggerService,
      SecurityService securityService) {
    this.triggerRepository = triggerRepository;
    this.triggerService = triggerService;
    this.securityService = securityService;
  }

  private void assertIsStaffOrOwner() throws UnauthenticatedException, UnauthorizedException {
    try {
      securityService.assertHasPermission(LemursRole.STAFF);
    } catch (UnauthorizedException e) {
      // If not staff, check if owner. If not owner either, this will throw and bubble up.
      // An UnauthenticatedException would have been thrown and passed up already, which is correct.
      securityService.assertHasPermission(LemursRole.OWNER);
    }
  }

  public List<DangerAlertTriggerDto> getAllTriggers()
      throws UnauthenticatedException, UnauthorizedException {
    assertIsStaffOrOwner();
    
    return triggerRepository.findAll()
        .stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  public DangerAlertTriggerDto getTrigger(Integer id) 
      throws EntityDoesNotExistException, UnauthenticatedException, UnauthorizedException {
    assertIsStaffOrOwner();
    
    Optional<DangerAlertTrigger> triggerOpt = triggerRepository.findById(id);
    if (!triggerOpt.isPresent()) {
      throw new EntityDoesNotExistException("Danger alert trigger with ID " + id + " does not exist.");
    }
    
    return convertToDto(triggerOpt.get());
  }

  @Transactional
  public DangerAlertTriggerDto createTrigger(DangerAlertTriggerDto dto) 
      throws UnauthenticatedException, UnauthorizedException {
    assertIsStaffOrOwner();
    
    DangerAlertTrigger entity = new DangerAlertTrigger(
        null,
        dto.getQuestionId(),
        dto.getThreshold(),
        dto.getAlertMessage(),
        dto.getIsActive() != null ? dto.getIsActive() : true
    );
    
    entity = triggerRepository.save(entity);
    triggerService.refreshTriggers(); // Refresh cached triggers
    
    return convertToDto(entity);
  }

  @Transactional
  public DangerAlertTriggerDto updateTrigger(Integer id, DangerAlertTriggerDto dto) 
      throws EntityDoesNotExistException, UnauthenticatedException, UnauthorizedException {
    assertIsStaffOrOwner();
    
    Optional<DangerAlertTrigger> triggerOpt = triggerRepository.findById(id);
    if (!triggerOpt.isPresent()) {
      throw new EntityDoesNotExistException("Danger alert trigger with ID " + id + " does not exist.");
    }
    
    DangerAlertTrigger entity = triggerOpt.get();
    entity.setQuestionId(dto.getQuestionId());
    entity.setThreshold(dto.getThreshold());
    entity.setAlertMessage(dto.getAlertMessage());
    if (dto.getIsActive() != null) {
      entity.setIsActive(dto.getIsActive());
    }
    
    entity = triggerRepository.save(entity);
    triggerService.refreshTriggers(); // Refresh cached triggers
    
    return convertToDto(entity);
  }

  @Transactional
  public void deleteTrigger(Integer id) 
      throws EntityDoesNotExistException, UnauthenticatedException, UnauthorizedException {
    assertIsStaffOrOwner();

    if (!triggerRepository.existsById(id)) {
      throw new EntityDoesNotExistException("Danger alert trigger with ID " + id + " does not exist.");
    }
    
    triggerRepository.deleteById(id);
    triggerService.refreshTriggers(); // Refresh cached triggers
  }
  
  private DangerAlertTriggerDto convertToDto(DangerAlertTrigger entity) {
    return new DangerAlertTriggerDto(
        entity.getId(),
        entity.getQuestionId(),
        entity.getThreshold(),
        entity.getAlertMessage(),
        entity.getIsActive()
    );
  }
  
  // For dependency injection
  public DangerAlertTriggerService getTriggerService() {
    return triggerService;
  }
}
