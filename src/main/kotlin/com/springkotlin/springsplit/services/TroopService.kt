package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User

interface TroopService {
    fun createTroop(troopInfo:AddUserDTO,username: String):TroopDetailsDTO
    fun allTroopsofUser(username: String):List<TroopDetailsDTO>
    fun allTroops():List<TroopDetailsDTO>
    fun addUserInTroop(addUserDTO: AddUserDTO,username: String):TroopDetailsDTO
}