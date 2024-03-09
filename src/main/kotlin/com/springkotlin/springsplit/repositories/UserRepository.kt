package com.springkotlin.springsplit.repositories

import com.springkotlin.springsplit.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository:JpaRepository<User,Int> {

    fun findByEmail(email: String):User
    fun findByEmailAndPassword(email:String,password:String):User?



}