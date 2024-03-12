package com.springkotlin.springsplit.repositories

import com.springkotlin.springsplit.entities.Roles
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository:JpaRepository<Roles,Int> {

    fun findByName(role:String):Roles?
}