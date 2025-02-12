/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.microsoft;

import edu.wpi.lemurs.api.endpoints.user.User;
import java.util.Objects;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/** A {@link AuthMicrosoftAuthentication} represents a successful microsoft authentication token. */
public class AuthMicrosoftAuthentication extends AbstractAuthenticationToken {
  private transient String accessToken;
  private final transient User user;

  /**
   * Creates a {@link AuthMicrosoftAuthentication} with the google access token and the
   * authenticated {@link User}.
   *
   * @param accessToken The google access token.
   * @param person The authenticated {@link User}.
   */
  public AuthMicrosoftAuthentication(String accessToken, User user) {
    super(AuthorityUtils.NO_AUTHORITIES);
    this.accessToken = accessToken;
    this.user = user;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public Object getCredentials() {
    return accessToken;
  }

  @Override
  public Object getPrincipal() {
    return user;
  }

  @Override
  public void eraseCredentials() {
    super.eraseCredentials();
    accessToken = null;
  }

  @Override
  public boolean equals(Object obj) {
    if (this.getClass() != obj.getClass()) {
      return false;
    }

    AuthMicrosoftAuthentication authMicrosoft = (AuthMicrosoftAuthentication) obj;

    return this.accessToken.equals(authMicrosoft.accessToken)
        && this.user.equals(authMicrosoft.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessToken, user);
  }
}
