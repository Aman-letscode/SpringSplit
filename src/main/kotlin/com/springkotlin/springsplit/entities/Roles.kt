package com.springkotlin.springsplit.entities

import jakarta.persistence.*

@Entity
@Table(name="roles")
data class Roles(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id:Int,

    @Column(name = "name")
    val name:String


)
