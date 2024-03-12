package com.springkotlin.springsplit.config

import com.springkotlin.springsplit.entities.Roles
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.stream.Collectors


@Service
class CustomUserDetailsService(@Autowired val userRepository: UserRepository): UserDetailsService{
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByEmail(username)!!
        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            mapRolesToAuthorities(user.role)
        )
    }
    private fun mapRolesToAuthorities(roles:Roles): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(roles.name))
    }


}