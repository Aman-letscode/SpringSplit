package com.springkotlin.springsplit.config

import com.jobapplication.example.jobapplication.security.JWTAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.beans.Customizer


@Configuration
@EnableWebSecurity
//@EnableConfigurationProperties(JWTConfig::class)
class SecurityConfig{

    private var authEntryPoint: JWTAuthEntryPoint = JWTAuthEntryPoint()

    @Autowired
    private lateinit var customUserDetailsService:CustomUserDetailsService

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()



    
    @Bean
    open fun filterChain(http: HttpSecurity):SecurityFilterChain{
        http.invoke {
            cors{}
            csrf { disable() }
            exceptionHandling { authenticationEntryPoint = authEntryPoint }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeRequests {
                authorize("/api/auth/allUsers",hasAuthority("ADMIN"))
                authorize("/api/auth/register",permitAll)
                authorize("/api/auth/login",permitAll)
                authorize("/api/troop/showTroops",hasAuthority("ADMIN"))
                authorize("/api/troop/createTroop",hasAuthority("USER"))
                authorize("/api/troop/addUsers",hasAuthority("USER"))
                authorize("/api/troop/user",hasAuthority("USER"))
                authorize("/api/payment/createxpense",hasAuthority("USER"))
                authorize("/api/payment/summary",hasAuthority("USER"))
                authorize("/api/payment/paydue",hasAuthority("USER"))
                authorize("/api/payment/{Id}",hasAuthority("USER"))
                authorize("/api/payment/user",hasAuthority("USER"))
                authorize("/api/payment/deleteSplit/{splitId}",hasAuthority("USER"))
                authorize(anyRequest,authenticated)

            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(filter = jwtAuthenticationFilter())


        }
        return http.build()
    }

    @Bean
    fun users():UserDetailsService{
        val admin:UserDetails = User
            .builder()
            .username("admin")
            .password("password")
            .roles("ADMIN")
            .build()

        val user:UserDetails = User
            .builder()
            .username("user")
            .password("password")
            .roles("USER")
            .build()

        return InMemoryUserDetailsManager(admin,user)
    }


    @Bean
    fun jwtAuthenticationFilter(): JWTAuthenticationFilter {
        return JWTAuthenticationFilter()
    }


    @Bean
    fun authenticationManager(
        authenticationConfiguration: AuthenticationConfiguration,
    ): AuthenticationManager {
        return authenticationConfiguration.getAuthenticationManager()
    }




}