package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.config.JWTGenerator
import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Roles
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.RoleRepository
import com.springkotlin.springsplit.repositories.UserRepository
import com.springkotlin.springsplit.services.EntityFunctions
import com.springkotlin.springsplit.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.nio.file.FileAlreadyExistsException


@Service
class UserServiceImpl : UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var passwordEncoder: BCryptPasswordEncoder;

    @Autowired
    private lateinit var jwtGenerator: JWTGenerator

    @Autowired
    private val entityFunctions: EntityFunctions = EntityFunctions()


    override fun login(credentials: LoginDTO): AuthTokenDTO {

//        if (!entityFunctions.checkEmail(credentials.email)) throw Exception("Email Format is Incorrect")
        val token = jwtGenerator.generateToken(credentials.email)
        val foundUser: User = userRepository.findByEmail(credentials.email) ?: throw NotFoundException()
        if (!passwordEncoder.matches(
                credentials.password,
                foundUser.password
            )
        ) throw Exception("Password Doesn't Match")
        return AuthTokenDTO(token)
    }

    override fun createUser(userDTO: UserDTO): UserDTO {

//        if (!entityFunctions.checkEmail(userDTO.email)) throw Exception("Email Format is Incorrect")
        val foundUser: User? = userRepository.findByEmail(userDTO.email)
        if (foundUser != null) throw FileAlreadyExistsException("User Already Exist")

        val role: Roles = roleRepository.findByName(userDTO.role)
        val encodedPassword: String = passwordEncoder.encode(userDTO.password)
        val user = User(userDTO.name, userDTO.email, encodedPassword, role)
        return UserToUserDTO(userRepository.save(user))
    }

    fun displayAllUser(): List<UserDTO> = userRepository.findAll().map { it -> UserToUserDTO(it) }


}
