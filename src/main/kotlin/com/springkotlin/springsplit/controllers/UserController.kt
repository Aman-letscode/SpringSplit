package com.springkotlin.springsplit.controllers

import com.springkotlin.springsplit.dto.ExpenseDetails
import com.springkotlin.springsplit.dto.LoginDTO
import com.springkotlin.springsplit.dto.UserDTO
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.services.implement.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class UserController{

    @Autowired
    private lateinit var userService:UserServiceImpl



    @GetMapping("/register")
    fun register():String = "Welcome to Re"

    @GetMapping("/login")
    fun login():String = "Welcome to Login Portal"


    @PostMapping("/login")
    fun checkUser(@RequestBody userCredentials: LoginDTO):ResponseEntity<Any> = ResponseEntity.ok(userService.login(userCredentials))




    @PostMapping("/register")
    fun createUser(@RequestBody userDTO: UserDTO):ResponseEntity<Any> = ResponseEntity.ok(userService.createUser(userDTO))



    @GetMapping("/allusers")
    fun addUsers():ResponseEntity<List<UserDTO>> = ResponseEntity.ok(userService.displayAllUser())

}
