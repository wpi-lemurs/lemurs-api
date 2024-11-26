/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.microsoft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import edu.wpi.lemurs.api.EnvironmentService;
import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.endpoints.user.UserService;
import edu.wpi.lemurs.api.exceptions.BadExternalCommunicationException;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.ImpossibleRuntimeException;
import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.auth.email.AuthorizedEmailService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import edu.wpi.lemurs.api.security.roles.RoleService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Locator;
import jakarta.transaction.Transactional;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

  private EnvironmentService env;
  private AuthMicrosoftRepository authMicrosoftRepository;
  private AuthorizedEmailService authorizedEmailService;
  private UserService userService;
  private RoleService roleService;

  /** Autowires an {@link AuthMicrosoftService}. */
  @Autowired
  public AuthMicrosoftService(
      EnvironmentService env,
      AuthMicrosoftRepository authMicrosoftRepository,
      UserService userService,
      AuthorizedEmailService authorizedEmailService,
      RoleService roleService) {
    this.env = env;
    this.authMicrosoftRepository = authMicrosoftRepository;
    this.userService = userService;
    this.authorizedEmailService = authorizedEmailService;
    this.roleService = roleService;
  }

  /**
   * Attempts to login a user given a microsoft access token.
   *
   * @param accessToken The microsoft access token.
   * @return A successful {@link AuthMicrosoftAuthentication}.
   * @throws BadCredentialsException Thrown if the access token is not valid, or there is no user
   *     for this microsoft account.
   */
  public Authentication login(String accessToken) throws BadCredentialsException {
    String idToken = accessToken;
    MicrosoftID microsoftID = getMicrosoftID(idToken);

    User user;
    try {
      user = getUser(microsoftID.getId());
    } catch (BadCredentialsException e) {
      try {
        user = attemptAddUser(microsoftID);
      } catch (BadCredentialsException
          | EntityDoesNotExistException
          | UnauthenticatedException
          | UnauthorizedException e1) {
        throw new BadCredentialsException("Invalid Microsoft Key");
      }
    }

    userService.assertEnabledUser(user);

    return new AuthMicrosoftAuthentication(idToken, user);
  }

  /**
   * Throws an exception if the the credentials were updated after the token was issued.
   *
   * @param userID The user to check.
   * @param issuedAt The issue date to check against.
   * @return The {@link Date} when the user's credentials were last updated.
   * @throws EntityDoesNotExistException Thrown if the user does not have these credentials.
   * @throws UnauthenticatedException Throw if the credentials were updated since this refresh token
   *     was issued.
   */
  public void assertValidRefreshDate(Integer userID, Date issuedAt)
      throws EntityDoesNotExistException, UnauthenticatedException {
    Optional<AuthMicrosoft> authMicrosoft = authMicrosoftRepository.findByUserID(userID);
    if (authMicrosoft.isEmpty()) {
      throw new EntityDoesNotExistException();
    }
    Date lastUpdated = authMicrosoft.get().getUpdated();
    if (!issuedAt.after(lastUpdated)) {
      throw new UnauthenticatedException();
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
  private MicrosoftID getMicrosoftID(String idToken) throws BadCredentialsException {

    // TODO: Make this code better: return better error codes, only grab data once per day, maybe
    // seperate into smaller methods.
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

    Locator<Key> locator = new MicrosoftKeyLocator(env);

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

    String microsoftID = claims.get("oid").toString();
    String email = claims.get("upn").toString();

    return new MicrosoftID(microsoftID, email);
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

  /**
   * Attempts to add a {@link User} based on if their email is in the system.
   *
   * @param microsoftID The microsoft id.
   * @return The {@link User} linked to the microsoft id.
   * @throws BadCredentialsException Thrown if there is no email enabled this microsoft id.
   * @throws EntityDoesNotExistException
   * @throws UnauthorizedException
   * @throws UnauthenticatedException
   */
  @Transactional
  private User attemptAddUser(MicrosoftID microsoftID)
      throws BadCredentialsException,
          EntityDoesNotExistException,
          UnauthenticatedException,
          UnauthorizedException {
    String umassID = authorizedEmailService.getUmassID(microsoftID.getEmail());
    authorizedEmailService.deauthorizeWithoutAuthCheck(microsoftID.getEmail());

    User user = userService.createUserWithoutAuthCheck(umassID);
    authMicrosoftRepository.save(new AuthMicrosoft(microsoftID.getId(), user.getId(), new Date()));
    roleService.addRoleWithoutAuthCheck(
        user.getId(), LemursRole.USER); // TODO: This roles should be based on what the adder says.

    return user;
  }

  /** A {@link MicrosoftID} represents the combined user id and email of a user. */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static class MicrosoftID {
    private String id;
    private String email;
  }
}
