package com.springkotlin.springsplit.entities

import jakarta.persistence.*
import org.antlr.v4.runtime.misc.IntegerList
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime


@Entity
@Table(name="user")
data class User(

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id:Int,

        @Column(name="name")
        val name:String,

        @Column(name="email")
        val email:String,

        @Column(name="password")
        val password:String,

        @ManyToMany
        @Column(name="group")
        val group_envolved:List<Int>,

        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @JoinTable(
                name = "user_group",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "group_id", referencedColumnName = "id")]
        )
        var groupIn: MutableSet<Group> = mutableSetOf(),


        @CreationTimestamp
        val createdAt:LocalDateTime = LocalDateTime.now()



)
