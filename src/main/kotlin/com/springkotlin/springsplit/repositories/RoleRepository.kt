package com.springkotlin.springsplit.repositories

import com.springkotlin.springsplit.entities.Roles
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository:JpaRepository<Roles,Long> {

    fun findByName(role:String):Roles
}