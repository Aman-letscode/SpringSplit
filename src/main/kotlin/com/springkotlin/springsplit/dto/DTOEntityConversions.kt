package com.springkotlin.springsplit.dto

import com.springkotlin.springsplit.entities.Payment
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User

fun PaymentToPaymentDTO(payment: Payment): PaymentDTO = PaymentDTO(
    splitId = payment.splitId!!,
    amount = payment.amount,
    refunder = UserToUserEmailDTO(payment.refunder!!),
    receiver = UserToUserEmailDTO(payment.receiver!!),
    troop = TroopToTroopDetailsDTO(payment.troop!!),
    status = payment.status!!
)

fun TroopToTroopDetailsDTO(troop: Troop): TroopDetailsDTO =
    TroopDetailsDTO(
        name = troop.name,
        userList = troop.users.map{ UserToUserEmailDTO(it) }.toMutableSet(),
        totalAmountTransacted = troop.totalAmount
    )

fun UserToUserEmailDTO(user: User): UserEmailDTO =
    UserEmailDTO(
        name = user.name,
        email = user.email
    )

fun UserToUserDTO(user: User): UserDTO =
    UserDTO(
        name = user.name,
        email = user.email,
        password = user.password,
        role = user.role.name
    )
