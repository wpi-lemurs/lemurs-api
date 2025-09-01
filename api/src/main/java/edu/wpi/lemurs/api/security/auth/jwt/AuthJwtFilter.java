/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.jwt;

import edu.wpi.lemurs.api.endpoints.user.User;
import edu.wpi.lemurs.api.endpoints.user.UserService;
import edu.wpi.lemurs.api.exceptions.EntityDoesNotExistException;
import edu.wpi.lemurs.api.exceptions.ImpossibleRuntimeException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A {@link AuthJwtFilter} authenticates request with a valid Jwt bearer token in the Authorization
 * header.
 */
@Component
public class AuthJwtFilter extends OncePerRequestFilter {

  private JwtService jwtService;
  private UserService userService;

  /** Autowires a {@link AuthJwtFilter} */
  @Autowired
  public AuthJwtFilter(JwtService jwtService, UserService userService) {
    this.jwtService = jwtService;
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = getTokenFromRequest(request);

    if (!StringUtils.hasText(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      jwtService.assertValidAccessToken(token);
    } catch (JwtException e) {
      // If the token is invalid, send a 401 Unauthorized response and stop the filter chain.
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT Token");
      return;
    }

    Integer id = jwtService.getId(token);
    User user;
    try {
      user = userService.getUserWithoutAuthCheck(id);
    } catch (EntityDoesNotExistException e) {
      throw new ImpossibleRuntimeException(e);
    }

    userService.assertEnabledUser(user);

    AuthJwt authenticationToken = new AuthJwt(token, user);
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    filterChain.doFilter(request, response);
  }

  /**
   * Get the token from the bearer token.
   *
   * @param request The http request.
   * @return The token if the request had a bearer token. Otherwise, null.
   */
  private String getTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7, bearerToken.length());
    }

    return null;
  }
}
