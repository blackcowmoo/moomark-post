package com.moomark.post.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.moomark.post.configuration.passport.PassportFilter;
import com.moomark.post.configuration.passport.PassportService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private final PassportService passportService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().csrf().disable().formLogin().disable().httpBasic().disable().logout().disable()
        .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        .and().authorizeRequests()
        .antMatchers(HttpMethod.GET, "/api/v1/posts").permitAll()
        .antMatchers(HttpMethod.GET, "/api/v1/posts/count").permitAll()
        .antMatchers(HttpMethod.GET, "/api/v1/post/{postId}").permitAll()
        .antMatchers(HttpMethod.GET, "/actuator/health").permitAll()
        .anyRequest().authenticated();

    http.addFilterBefore(new PassportFilter(passportService),
        UsernamePasswordAuthenticationFilter.class);
  }
}
