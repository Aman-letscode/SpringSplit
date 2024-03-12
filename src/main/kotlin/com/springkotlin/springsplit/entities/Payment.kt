package com.springkotlin.springsplit.entities

import jakarta.annotation.Nullable
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


        @Nullable
        @ManyToOne(fetch = FetchType.LAZY , cascade = [CascadeType.ALL])
        @JoinColumn(name="refunderId")
        val refunder:User,


        @Nullable
        @ManyToOne(fetch = FetchType.LAZY , cascade = [CascadeType.ALL])
        @JoinColumn(name="receiverId")
        val receiver:User,

        @Nullable
        @ManyToOne(fetch = FetchType.LAZY , cascade = [CascadeType.ALL])
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
