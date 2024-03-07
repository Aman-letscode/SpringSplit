package com.springkotlin.springsplit.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.validator.constraints.UniqueElements
import java.time.LocalDateTime

@Entity
@Table(name="troop")
data class Troop(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id:Int,

    @UniqueElements
    @Column(name="name")
    val name:String,


//        @ManyToMany(mappedBy = "groups",fetch = FetchType.LAZY)
//        val userEnvolved:Set<User>,



    @ManyToMany(cascade = arrayOf(CascadeType.ALL))
    @JoinTable(
        name = "troop_user",
        joinColumns = arrayOf(JoinColumn(name = "troop_id")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "user_id"))
    )
    var users: Set<User> = HashSet<User>(),

    @Column(name="totalAmountTransacted")
    val totalAmount:Int,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()


){
    constructor(name:String,users:Set<User>,totalAmount: Int):this(0,name,users,totalAmount)
}
