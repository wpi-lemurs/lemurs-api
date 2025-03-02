/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.services;

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

  /** Gets the WPI Microsoft tenant ID. */
  public String getWPIMicrosoftTenantID() {
    return System.getenv("LEMURS_MICROSOFT_TENANT_ID_WPI");
  }

  /** Gets the UMass Microsoft tenant ID. */
  public String getUMassMicrosoftTenantID() {
    return System.getenv("LEMURS_MICROSOFT_TENANT_ID_UMASS");
  }

  /** Gets the Microsoft app ID. */
  public String getMicrosoftAppID() {
    return System.getenv("LEMURS_MICROSOFT_APP_ID");
  }

  /** Gets the LEMURS sender email address. */
  public String getLemursEmailAddress() {
    return System.getenv("LEMURS_EMAIL_ADDRESS");
  }
}
