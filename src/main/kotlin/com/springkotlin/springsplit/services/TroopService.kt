package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.TroopDTO
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User

interface TroopService {

    fun createTroop(troopInfo:TroopDTO):String

    fun allTroopsofUser(user: User):List<Troop>
    fun allTroops():List<Troop>

    fun addUserInTroop(members: List<String>, troopName: String):String

}