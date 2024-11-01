/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.microsoft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.endpoints.user.UserService;
import edu.wpi.lemurs.api.exceptions.BadExternalCommunicationException;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.ImpossibleRuntimeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Locator;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** The {@link AuthService} for microsoft authentication. */
@Service
public class AuthMicrosoftService {

  private AuthMicrosoftRepository authMicrosoftRepository;
  private UserService userService;

  /** Autowires an {@link AuthMicrosoftService}. */
  @Autowired
  public AuthMicrosoftService(
      AuthMicrosoftRepository authMicrosoftRepository, UserService userService) {
    this.authMicrosoftRepository = authMicrosoftRepository;
    this.userService = userService;
  }

  public Authentication login(MicrosoftLoginDto microsoftLoginDto) throws BadCredentialsException {
    try {
      String idToken = microsoftLoginDto.getCode();
      String microsoftID = getMicrosoftID(idToken);
      User user = getUser(microsoftID);

      // TODO: checkAccountStatus(user);

      return new AuthMicrosoftAuthentication(idToken, user);
    } catch (Exception e) {
      throw new BadCredentialsException("Invalid Microsoft Key");
    }
  }

  /**
   * Gets a microsoft id from an id token.
   *
   * @param idToken The id token.
   * @return The microsoft id.
   * @throws BadExternalCommunicationException Thrown if communication with microsoft failed.
   * @throws BadCredentialsException Thrown if microsoft id token is invalid.
   */
  private String getMicrosoftID(String idToken) throws BadCredentialsException {

    String url =
        "https://login.microsoftonline.com/"
            + System.getenv("LEMURS_MICROSOFT_TENANT_ID")
            + "/v2.0/.well-known/openid-configuration";
    HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
    String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();
    ResponseEntity<String> response;
    try {
      response =
          new RestTemplate().exchange(urlTemplate, HttpMethod.GET, requestEntity, String.class);
    } catch (RestClientException e) {
      throw new BadCredentialsException("");
    }
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new BadCredentialsException("");
    }
    String info = response.getBody();
    JsonObject jsonObject;
    try {
      jsonObject = JsonParser.parseString(info).getAsJsonObject();
    } catch (JsonParseException | IllegalStateException e) {
      throw new BadCredentialsException("");
    }
    String issuer = jsonObject.get("issuer").getAsString();

    Locator<Key> locator = new MicrosoftKeyLocator();

    Claims claims =
        Jwts.parser().keyLocator(locator).build().parseSignedClaims(idToken).getPayload();

    Set<String> aud = claims.getAudience();
    if (!aud.contains(System.getenv("LEMURS_MICROSOFT_APP_ID"))) {
      throw new BadCredentialsException("Invalid client authenticator.");
    }

    String iss = claims.getIssuer();
    if (!iss.equals(issuer)) {
      throw new BadCredentialsException("Wrong issuer.");
    }

    Date expirationDate = claims.getExpiration();
    Date currentDate = new Date();
    if (!expirationDate.after(currentDate)) {
      throw new BadCredentialsException("Credential expired.");
    }

    return claims.getSubject();
  }

  /**
   * Gets a {@link User} from a microsoft id.
   *
   * @param microsoftID The microsoft id.
   * @return The {@link User} linked to the microsoft id.
   * @throws BadCredentialsException Thrown if there is no {@link User} linked to this microsoft id.
   */
  private User getUser(String microsoftID) throws BadCredentialsException {
    Optional<AuthMicrosoft> optionalID = authMicrosoftRepository.findById(microsoftID);
    if (optionalID.isEmpty()) {
      throw new BadCredentialsException("Microsoft token did not have an id.");
    }

    Integer id = optionalID.get().getUserID();
    if (id == null) {
      throw new BadCredentialsException("No user with this microsoft account.");
    }

    try {
      return userService.getUser(id);
    } catch (EntityDoesNotExistException e) {
      throw new ImpossibleRuntimeException(e);
    }
  }

  public void updateCredentials(Integer userID, String code)
      throws BadCredentialsException, BadExternalCommunicationException {
    String idToken = code;
    String googleID = getMicrosoftID(idToken);
    AuthMicrosoft authMicrosoft = new AuthMicrosoft(googleID, userID);
    authMicrosoftRepository.save(authMicrosoft);
  }
}
