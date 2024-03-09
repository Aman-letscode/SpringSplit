package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.ExpenseDetails
import com.springkotlin.springsplit.dto.Login
import com.springkotlin.springsplit.dto.UserDTO

interface UserService {
    fun login(credentials:Login):String

    fun createUser(userDTO:UserDTO):UserDTO
    fun expenseDetails(credentials: Login):ExpenseDetails
}