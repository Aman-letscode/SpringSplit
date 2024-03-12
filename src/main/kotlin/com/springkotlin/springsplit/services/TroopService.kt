package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User

interface TroopService {

    fun createTroop(troopInfo:AddUserDTO):String

    fun allTroopsofUser(userEmailDTO: UserEmailDTO):List<Any>
    fun allTroops():List<Any>
//    fun allTroops():List<TroopDetailsDTO>

    fun addUserInTroop(members: List<String>, troopName: String):String

    fun allPaymentsOfTroop(troopName:String):List<PaymentDTO>

    fun allUnpaidPaymentsofTroops(troop:TroopDTO):List<PaymentDTO>

}