package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.Login
import com.springkotlin.springsplit.dto.UserInfo

interface UserService {
    fun login(credentials:Login):String

    fun createUser(userInfo:UserInfo):UserInfo
}