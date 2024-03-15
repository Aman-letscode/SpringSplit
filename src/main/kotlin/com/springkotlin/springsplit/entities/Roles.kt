package com.springkotlin.springsplit.entities

import jakarta.persistence.*

@Entity
@Table(name = "roles")
data class Roles(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "name")
    val name: String,


    )
