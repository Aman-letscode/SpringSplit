package com.springkotlin.springsplit.controllers

import com.springkotlin.springsplit.dto.TroopDTO
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.services.implement.TroopServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/troop")
class TroopController(@Autowired var troopServiceImpl: TroopServiceImpl) {

    @GetMapping("/createTroop")
    fun create():String{
        return "Welcome to creation of Troops"
    }
    @GetMapping("/showTroops")
    fun showTroops():List<Troop>{

        return troopServiceImpl.allTroops()
    }

    @PostMapping("/createTroop")
    fun createTroop(@RequestBody troopData:TroopDTO):String{
        return troopServiceImpl.createTroop(troopData)
    }


    @PostMapping("/addUsers")
    fun addUsers(@RequestBody troopData:TroopDTO):String{
        return troopServiceImpl.addUserInTroop(troopData.user_list,troopData.name)
    }


}