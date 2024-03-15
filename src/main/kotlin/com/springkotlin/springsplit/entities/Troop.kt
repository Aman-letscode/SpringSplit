package com.springkotlin.springsplit.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.validator.constraints.UniqueElements
import java.time.LocalDateTime

@Entity
@Table(name = "troop")
data class Troop(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @UniqueElements
    @Column(name = "name", unique = true)
    val name: String,


    @ManyToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinTable(name = "troop_user")
    var users: MutableSet<User> = mutableSetOf(),

    @Column(name = "totalAmountTransacted")
    val totalAmount: Float,

    @OneToMany(mappedBy = "troop", fetch = FetchType.LAZY)
    var troopPayments: Set<Payment> = HashSet<Payment>(),


    @CreationTimestamp
    @Column(updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    val updatedAt: LocalDateTime = LocalDateTime.now(),


    ) {
    constructor(name: String, users: MutableSet<User>, totalAmount: Float) : this(0, name, users, totalAmount)
}
