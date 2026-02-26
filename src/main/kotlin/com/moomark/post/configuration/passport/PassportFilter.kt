package com.moomark.post.configuration.passport

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class PassportFilter(
    private val passportService: PassportService,
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val passport = httpRequest.getHeader("x-moom-passport-user")
        val key = httpRequest.getHeader("x-moom-passport-key")
        if (passport != null && key != null) {
            val user = passportService.parsePassport(passport, key)
            if (user != null) {
                val auth = getAuthentication(user)
                SecurityContextHolder.getContext().authentication = auth
            }
            // If user is null, leave SecurityContext unauthenticated
        }

        chain.doFilter(request, response)
    }

    private fun getAuthentication(user: User?): Authentication = UsernamePasswordAuthenticationToken(
        user,
        "",
        listOf(SimpleGrantedAuthority("ROLE_USER")),
    )
}
