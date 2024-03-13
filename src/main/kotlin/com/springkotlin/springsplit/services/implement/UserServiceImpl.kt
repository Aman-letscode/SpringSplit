package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.config.JWTGenerator
import com.springkotlin.springsplit.dto.AuthTokenDTO
import com.springkotlin.springsplit.dto.LoginDTO
import com.springkotlin.springsplit.dto.UserDTO
import com.springkotlin.springsplit.dto.UserToUserEmailDTO
import com.springkotlin.springsplit.entities.Roles
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.RoleRepository
import com.springkotlin.springsplit.repositories.UserRepository
import com.springkotlin.springsplit.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserServiceImpl :UserService{

    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var roleRepository: RoleRepository
    @Autowired lateinit var passwordEncoder: BCryptPasswordEncoder;

    //For Login
    @Autowired lateinit var authenticationManager:AuthenticationManager
    @Autowired lateinit var jwtGenerator: JWTGenerator

    override fun login(credentials: LoginDTO): Any {
//        val authentication: Authentication = authenticationManager.authenticate(
//            UsernamePasswordAuthenticationToken(
//                credentials.email,
//                credentials.password
//            )
//        )
//        SecurityContextHolder.getContext().authentication = authentication
        val token = jwtGenerator.generateToken(credentials.email)
        val foundUser:User = userRepository.findByEmail(credentials.email)?:return NotFoundException()
        if (passwordEncoder.matches(credentials.password,foundUser.password)) return AuthTokenDTO(token)
        else return "Invalid Password"
    }

    override fun createUser(userDTO: UserDTO):Any {

        val foundUser:User? = userRepository.findByEmail(userDTO.email)
        if(foundUser!=null) return "User Already Exists"

        val role: Roles = roleRepository.findByName(userDTO.role)
        val encodedPassword:String = passwordEncoder.encode(userDTO.password)
        val user = User(userDTO.name,userDTO.email,encodedPassword,role)
        var result:User = userRepository.save(user)
        return UserDTO(result.name,result.email,result.password,result.role.name)
    }
    fun displayAllUser():List<User> = userRepository.findAll()




}
