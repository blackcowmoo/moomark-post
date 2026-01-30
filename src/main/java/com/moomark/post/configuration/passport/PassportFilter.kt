package com.moomark.post.configuration.passport;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PassportFilter extends GenericFilterBean {
  private final PassportService passportService;

  @Override
  public void doFilter(
          ServletRequest request,
          ServletResponse response,
          FilterChain chain
  ) throws ServletException, IOException {
    String passport = ((HttpServletRequest) request).getHeader("x-moom-passport-user");
    String key = ((HttpServletRequest) request).getHeader("x-moom-passport-key");

    if (passport != null && key != null) {
      User user = passportService.parsePassport(passport, key);
      Authentication auth = getAuthentication(user);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    chain.doFilter(request, response);
  }

  public Authentication getAuthentication(User user) {
    return new UsernamePasswordAuthenticationToken(user, "",
        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
  }
}
