package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.ExpenseDetails
import com.springkotlin.springsplit.dto.LoginDTO
import com.springkotlin.springsplit.dto.UserDTO
import com.springkotlin.springsplit.entities.User

interface UserService {
    fun login(credentials:LoginDTO): Any

    fun createUser(userDTO:UserDTO):Any

}