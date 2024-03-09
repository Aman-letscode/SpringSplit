package com.springkotlin.springsplit.entities

import jakarta.persistence.*
import org.antlr.v4.runtime.misc.IntegerList
import org.hibernate.annotations.CreationTimestamp
import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime


@Entity
@Table(name="user")
data class User(

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id:Int,

        @Column(name="name")
        var name:String,

        @Column(name="email",unique = true)
        var email:String,

        @Column(name="password")
        var password:String,


        @ManyToMany(mappedBy = "users",cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var troops: MutableSet<Troop> = mutableSetOf(),


        @OneToMany(mappedBy = "refunder", fetch = FetchType.LAZY)
        var refunderPayments:Set<Payment> = HashSet<Payment>(),

        @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
        var receiverPayments:Set<Payment> = HashSet<Payment>(),


        @CreationTimestamp @NotNull
        val createdAt:LocalDateTime = LocalDateTime.now()



){
        constructor(name:String,email:String,password:String) : this(0,name,email,password)
}

