package com.springkotlin.springsplit.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime


@Entity
@Table(name="payment")
data class Payment(


        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id:Int,

        @Column(name="amount")
        val amount:Int,


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="refunder")
        val refunder:User,


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="reciever")
        val receiver:User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="groupId")
        val groupId:Group,

        @CreationTimestamp
        val createdAt: LocalDateTime = LocalDateTime.now()


)
