package com.springkotlin.springsplit.repositories

import com.springkotlin.springsplit.entities.Troop
import org.springframework.data.jpa.repository.JpaRepository

interface TroopRepository:JpaRepository<Troop,Int> {
    fun findByName(name:String):Troop


}