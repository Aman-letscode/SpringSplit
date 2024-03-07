package com.springkotlin.springsplit.controllers

import com.springkotlin.springsplit.dto.Login
import com.springkotlin.springsplit.dto.UserInfo
import com.springkotlin.springsplit.services.implement.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController{

    @Autowired
    private lateinit var userService:UserServiceImpl



    @GetMapping("/register")
    fun register():String{
        return "Welcome to Re"
    }
    @GetMapping("/login")
    fun login():String{
        return "Welcome to Login Portal"
    }

    @PostMapping("/login")
    fun checkUser(@RequestBody userCredentials: Login):String{
        return userService.login(userCredentials)
//        return "Welcome to Login Portal"
    }


    @PostMapping("/register")
    fun createUser(@RequestBody userInfo: UserInfo):UserInfo{
        return userService.createUser(userInfo)
//        return result
    }
}
