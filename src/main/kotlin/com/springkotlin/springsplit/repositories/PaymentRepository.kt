package com.springkotlin.springsplit.repositories

import com.springkotlin.springsplit.entities.Payment
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository:JpaRepository<Payment,Int> {
    fun findByRefunder(refunder: User):List<Payment>
    fun findByReceiver(receiver:User):List<Payment>
    fun findByTroop(troop: Troop):List<Payment>
}