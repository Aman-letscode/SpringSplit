package com.springkotlin.springsplit.dto

import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import javax.sound.midi.Receiver

data class PaymentDTO(
    val amount:Float,
    val refunder: UserEmailDTO,
    val receiver: UserEmailDTO,
    val troop: TroopDetailsDTO,
    val status:String


    )
