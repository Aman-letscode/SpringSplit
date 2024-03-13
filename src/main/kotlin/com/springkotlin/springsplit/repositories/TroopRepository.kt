package com.springkotlin.springsplit.repositories

import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query

interface TroopRepository:JpaRepository<Troop,Int>, JpaSpecificationExecutor<Troop> {
    fun findByName(name:String):Troop

    fun existsByNameAndUsers(name:String,user:User):Boolean
    fun existsByName(name:String):Boolean
    fun findByUsers(user: User):List<Troop>
    @Query(
        value = "SELECT u.id AS user_id, " +
                "t.id AS troop_id, t.created_at AS troop_created_at, t.name AS troop_name " +
                "FROM user u " +
                "JOIN troop_user ut ON u.id = ut.users_id " +
                "JOIN troop t ON ut.troops_id = t.id",
        nativeQuery = true
    )
    fun findTroops():List<Array<Any>>

    override fun findAll(spec: Specification<Troop>): List<Troop>
}