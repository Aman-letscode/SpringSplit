package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User

interface TroopService {

    fun createTroop(troopInfo:AddUserDTO,token:String):String

    fun allTroopsofUser(userEmailDTO: UserEmailDTO,token: String):List<Any>
    fun allTroops():List<Any>
//    fun allTroops():List<TroopDetailsDTO>

    fun addUserInTroop(addUserDTO: AddUserDTO,token: String):String

//    fun allPaymentsOfTroop(troopName:String):List<PaymentDTO>
//
//    fun allUnpaidPaymentsofTroops(troop:TroopDTO):List<PaymentDTO>

}