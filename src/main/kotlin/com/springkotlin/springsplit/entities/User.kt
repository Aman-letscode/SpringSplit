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

        @Column(name="email")
        var email:String,

        @Column(name="password")
        var password:String,


        @ManyToMany(mappedBy = "users", cascade = [CascadeType.ALL])
        var troops: Set<Troop> = HashSet<Troop>(),



        @CreationTimestamp @NotNull
        val createdAt:LocalDateTime = LocalDateTime.now()



){
        constructor(name:String,email:String,password:String) : this(0,name,email,password)
}

