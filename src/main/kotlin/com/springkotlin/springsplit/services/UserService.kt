package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.User

interface UserService {
    fun login(credentials:LoginDTO): AuthTokenDTO

    fun createUser(userDTO:UserDTO):UserDTO

}