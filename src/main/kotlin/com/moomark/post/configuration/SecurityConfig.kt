package com.moomark.post.configuration

import com.moomark.post.configuration.passport.PassportFilter
import com.moomark.post.configuration.passport.PassportService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val passportService: PassportService,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .csrf()
            .disable()
            .formLogin()
            .disable()
            .httpBasic()
            .disable()
            .logout()
            .disable()
            .exceptionHandling()
            .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/api/v1/posts")
            .permitAll()
            .antMatchers(HttpMethod.GET, "/api/v1/posts/count")
            .permitAll()
            .antMatchers(HttpMethod.GET, "/api/v1/post/*")
            .permitAll()
            .antMatchers(HttpMethod.GET, "/actuator/health")
            .permitAll()
            .anyRequest()
            .authenticated()

        http.addFilterBefore(
            PassportFilter(passportService),
            UsernamePasswordAuthenticationFilter::class.java,
        )

        return http.build()
    }
}
