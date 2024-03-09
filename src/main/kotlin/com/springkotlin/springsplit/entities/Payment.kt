package com.springkotlin.springsplit.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime


@Entity
@Table(name="payment")
data class Payment(


        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id:Int,

        @Column(name="amount")
        val amount:Float,


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="refunderId")
        val refunder:User,


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="receiverId")
        val receiver:User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="troopId")
        val troop: Troop,

        @Column(name = "status")
        var status:String,

        @CreationTimestamp
        @Column(updatable = false)
        val createdAt: LocalDateTime = LocalDateTime.now(),

        @UpdateTimestamp
        val updatedAt: LocalDateTime = LocalDateTime.now()
){
        constructor(amount:Float,refunder: User,receiver: User,troop: Troop,status: String):this(0,amount,refunder,receiver,troop,status)
}
