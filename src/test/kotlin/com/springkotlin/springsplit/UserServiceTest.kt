package com.springkotlin.springsplit

import com.springkotlin.springsplit.controllers.UserController
import com.springkotlin.springsplit.dto.LoginDTO
import com.springkotlin.springsplit.dto.UserDTO
import com.springkotlin.springsplit.dto.UserToUserDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity


@SpringBootTest
class UserServiceTest {

    @Autowired
    private lateinit var userController:UserController

    @Test
    fun getTest(){
        println(userController.login())
        println(userController.register())
        val result:ResponseEntity<UserDTO> = userController.createUser(UserDTO("aryaman","arya@gmail.com","aryaman","USER"))
        println(result.toString())
//        println(userController.checkUser(LoginDTO("shreyas@gmail.com","shreyas123")).toString())
    }
}