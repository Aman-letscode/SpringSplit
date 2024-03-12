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

            authorizeRequests {
                authorize("/api/auth/**",permitAll)
                authorize("/api/troop/showTroops",hasAuthority("ADMIN"))
                authorize("/api/troop/createTroop",permitAll)
                authorize("/api/troop/addUsers",permitAll)
                authorize("/api/troop/user",permitAll)
                authorize("/api/payment/createxpense",permitAll)
                authorize("/api/payment/user",permitAll)
                authorize("/api/payment/paydue",permitAll)
//                authorize(anyRequest,hasRole("ADMIN"))
                authorize(anyRequest,authenticated)

            }
//            httpBasic { defaults }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(filter = jwtAuthenticationFilter())
//            (JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)


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