/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api;

import org.springframework.stereotype.Service;

/**
 * Service for getting environment variables.
 *
 * @apiNote This service is created so that it is possible to mock this class.
 */
@Service
public class EnvironmentService {

  /** Gets the JWT signature. */
  public String getJwtSignature() {
    return System.getenv("LEMURS_SIGNATURE");
  }

  /** Gets the Microsoft tenant ID. */
  public String getMicrosoftTenantID() {
    return System.getenv("LEMURS_MICROSOFT_TENANT_ID");
  }

  /** Gets the Microsoft app ID. */
  public String getMicrosoftAppID() {
    return System.getenv("LEMURS_MICROSOFT_APP_ID");
  }
}
