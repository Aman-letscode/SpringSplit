package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.AddUserDTO
import com.springkotlin.springsplit.dto.ExpenseDetails
import com.springkotlin.springsplit.dto.PaymentDTO
import com.springkotlin.springsplit.dto.TroopDTO
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User

interface TroopService {

    fun createTroop(troopInfo:AddUserDTO):String

    fun allTroopsofUser(userId: Int):List<TroopDTO>
    fun allTroops():List<TroopDTO>

    fun addUserInTroop(members: List<String>, troopName: String):String

    fun allPaymentsOfTroop(troopName:String):List<PaymentDTO>

    fun allUnpaidPaymentsofTroops(troop:TroopDTO):List<PaymentDTO>

}