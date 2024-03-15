package com.springkotlin.springsplit.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint


class JWTAuthEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?,
    ) {
        if (authException != null && response != null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.message)
        }
    }

}
