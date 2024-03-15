package com.springkotlin.springsplit.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import kotlin.jvm.Transient


@Entity
@Table(name = "user")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int,

    @Column(name = "name")
    var name: String,

    @Column(name = "email", unique = true)
    var email: String,

    @JsonIgnore
    @Column(name = "password")
    var password: String,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "roles")
    val role: Roles,

    @ManyToMany(mappedBy = "users", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var troops: MutableSet<Troop>? = mutableSetOf(),


    @OneToMany(mappedBy = "refunder", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @Transient
    var refunderPayments: Set<Payment>? = HashSet<Payment>(),

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @Transient
    var receiverPayments: Set<Payment>? = HashSet<Payment>(),


    @CreationTimestamp @NotNull
    val createdAt: LocalDateTime? = LocalDateTime.now(),


    ) {
    constructor(name: String, email: String, password: String, role: Roles) : this(0, name, email, password, role)
}

