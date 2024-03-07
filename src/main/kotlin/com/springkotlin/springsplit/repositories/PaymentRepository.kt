package com.springkotlin.springsplit.repositories

import com.springkotlin.springsplit.entities.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository:JpaRepository<Payment,Int> {
}