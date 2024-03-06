package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.Login
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Service

class UserService{


    fun login(userCredentials:Login):String{

        try{
            

        }catch (e:Exception){
            throw e;
        }
        return "true"
    }
}