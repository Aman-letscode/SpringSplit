package com.springkotlin.springsplit.repositories

import com.springkotlin.springsplit.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository:JpaRepository<User,Long> {

    fun findByEmail(email: String):User?
    fun findByEmailAndPassword(email:String,password:String):User?



}