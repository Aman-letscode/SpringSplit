package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.config.JWTGenerator
import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Roles
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.RoleRepository
import com.springkotlin.springsplit.repositories.UserRepository
import com.springkotlin.springsplit.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserServiceImpl :UserService{

    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var roleRepository: RoleRepository
    @Autowired lateinit var passwordEncoder: BCryptPasswordEncoder;

    //For Login
    @Autowired lateinit var jwtGenerator: JWTGenerator
//    @Autowired lateinit var modelMapper: ModelMapper
    override fun login(credentials: LoginDTO): Any {

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
//        return modelMapper.map(result,UserDTO::class.java)
        return UserDTO(result.name,result.email,result.password,result.role.name)
    }
    fun displayAllUser():List<UserDTO> = userRepository.findAll().map { it -> UserToUserDTO(it) }




}
