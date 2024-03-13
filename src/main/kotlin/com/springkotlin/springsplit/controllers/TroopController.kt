package com.springkotlin.springsplit.controllers

import com.springkotlin.springsplit.dto.AddUserDTO
import com.springkotlin.springsplit.dto.TroopDTO
import com.springkotlin.springsplit.dto.TroopDetailsDTO
import com.springkotlin.springsplit.dto.UserEmailDTO
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.services.implement.TroopServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/troop")
class TroopController(@Autowired var troopServiceImpl: TroopServiceImpl) {

    @GetMapping("/createTroop")
    fun create():String{
        return "Welcome to creation of Troops"
    }
    @GetMapping("/showTroops")
    fun showTroops():ResponseEntity<Any>   = ResponseEntity.ok(troopServiceImpl.allTroops())
//
//    @GetMapping("/showTroops")
//    fun showTroops():ResponseEntity<Any>   = ResponseEntity.ok(troopServiceImpl.allTroopsBySpec())

    @PostMapping("/createTroop")
    fun createTroop(@RequestBody troopData:AddUserDTO,@RequestHeader("Authorization") token: String):String = troopServiceImpl.createTroop(troopData,token)

    @PutMapping("/addUsers")
    fun addUsers(@RequestBody addUser:AddUserDTO,@RequestHeader("Authorization") token: String):String = troopServiceImpl.addUserInTroop(addUser,token)

    @GetMapping("/user")
    fun troopsOfUser(@RequestBody userEmailDTO: UserEmailDTO,@RequestHeader("Authorization") token: String):List<Any>  = troopServiceImpl.allTroopsofUser(userEmailDTO,token)

}