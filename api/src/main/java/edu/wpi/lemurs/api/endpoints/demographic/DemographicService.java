/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.endpoints.demographic;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The {@link DemographicService} is a service that allows for {@link Demographic} management. */
@Service
@Transactional
public class DemographicService {

  private SecurityService securityService;
  private DemographicRepository demographicRepository;

  /** Autowires a {@link DemographicService}. */
  @Autowired
  public DemographicService(
      SecurityService securityService, DemographicRepository demographicRepository) {
    this.securityService = securityService;
    this.demographicRepository = demographicRepository;
  }

  /**
   * Gets all of the demographics for the user.
   *
   * @return A list of {@link DemographicResponse}s with each demographic.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public List<DemographicResponse> getDemographics()
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    List<DemographicResponse> demographics = new ArrayList<>();

    for (Demographic demographic :
        demographicRepository.findByUserID(securityService.getUser().getId())) {
      demographics.add(new DemographicResponse(demographic.getKeyword(), demographic.getValue()));
    }

    return demographics;
  }

  /**
   * Saves demographics from the user.
   *
   * @param demographicDto The demographic to add.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public void recordDemographic(List<DemographicDto> demographicDtos)
      throws UnauthenticatedException, UnauthorizedException {

    for (DemographicDto demographicDto : demographicDtos) {
      demographicRepository.save(
          new Demographic(
              securityService.getUser().getId(),
              demographicDto.getKeyword().toLowerCase(),
              demographicDto.getValue().toLowerCase()));
    }
  }

  /**
   * Gets all of the demographics for the user as a map.
   *
   * @return A map of demographic keyword to values.
   * @throws UnauthenticatedException Thrown if the user is not authenticated.
   * @throws UnauthorizedException Thrown if the user does not have {@code LemursRole.USER} role.
   */
  public Map<String, String> getDemographicMap()
      throws UnauthenticatedException, UnauthorizedException {
    securityService.assertHasRole(LemursRole.USER);

    HashMap<String, String> demographics = new HashMap<>();

    for (Demographic demographic :
        demographicRepository.findByUserID(securityService.getUser().getId())) {
      demographics.put(
          demographic.getKeyword().toLowerCase(), demographic.getValue().toLowerCase());
    }

    return demographics;
  }
}
