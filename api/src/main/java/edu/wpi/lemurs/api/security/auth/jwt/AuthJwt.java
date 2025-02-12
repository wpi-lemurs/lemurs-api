/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.jwt;

import edu.wpi.lemurs.api.endpoints.user.User;
import java.util.Objects;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/** An {@link AuthJwt} represents a successful JWT authentication token. */
public class AuthJwt extends AbstractAuthenticationToken {
  private transient String jwtToken;
  private final transient User user;

  /** Creates an authenticationed {@link AuthJwt} for the given access token and user. */
  public AuthJwt(String jwtToken, User user) {
    super(AuthorityUtils.NO_AUTHORITIES);
    this.jwtToken = jwtToken;
    this.user = user;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public Object getCredentials() {
    return jwtToken;
  }

  @Override
  public Object getPrincipal() {
    return user;
  }

  @Override
  public void eraseCredentials() {
    super.eraseCredentials();
    jwtToken = null;
  }

  @Override
  public boolean equals(Object obj) {
    if (this.getClass() != obj.getClass()) {
      return false;
    }

    AuthJwt authJwt = (AuthJwt) obj;

    return this.jwtToken.equals(authJwt.jwtToken) && this.user.equals(authJwt.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jwtToken, user);
  }
}
