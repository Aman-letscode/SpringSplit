package com.jobapplication.example.jobapplication.security

import com.springkotlin.springsplit.config.CustomUserDetailsService
import com.springkotlin.springsplit.config.JWTGenerator
import com.springkotlin.springsplit.services.implement.UserServiceImpl
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JWTAuthenticationFilter : OncePerRequestFilter() {
    @Autowired
    private lateinit var tokenGenerator: JWTGenerator

    @Autowired
    private lateinit var userServiceImpl:CustomUserDetailsService

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = getJWTFromRequest(request)
        if (StringUtils.hasText(token) && tokenGenerator.validateToken(token!!)) {
            val username: String = tokenGenerator.generateUserNameByJWT(token)
            val userDetails: UserDetails = userServiceImpl.loadUserByUsername(username)
            val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authenticationToken
        }
        filterChain.doFilter(request, response)
    }

    private fun getJWTFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7, bearerToken.length)
        } else null
    }
}