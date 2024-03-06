package com.springkotlin.springsplit.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime


@Entity
@Table(name="group")
data class Group(


        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id:Int,

        @Column(name="name")
        val name:String,


        @ManyToMany(mappedBy = "user_group",fetch = FetchType.LAZY)
        val userEnvolved:Set<User>,

        @Column(name="totalAmountTransacted")
        val totalAmount:Int,

        @CreationTimestamp
        val createdAt: LocalDateTime = LocalDateTime.now()


)
