package com.springkotlin.springsplit.controllers

import com.springkotlin.springsplit.dto.AddUserDTO
import com.springkotlin.springsplit.dto.TroopDTO
import com.springkotlin.springsplit.dto.TroopDetailsDTO
import com.springkotlin.springsplit.dto.UserEmailDTO
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.services.implement.TroopServiceImpl
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/troop")
class TroopController(@Autowired var troopServiceImpl: TroopServiceImpl) {

    @GetMapping("/createTroop")
    fun create(): String =
        "Welcome to creation of Troops"

    @GetMapping("/showTroops")
    fun showTroops(): ResponseEntity<List<TroopDetailsDTO>> =
        ResponseEntity.ok(troopServiceImpl.allTroops())

    @PostMapping("/createTroop")
    fun createTroop(@RequestBody troopData: AddUserDTO, request: HttpServletRequest): TroopDetailsDTO =
        troopServiceImpl.createTroop(troopData, request.getAttribute("username") as String)

    @PutMapping("/addUsers")
    fun addUsers(@RequestBody addUser: AddUserDTO, request: HttpServletRequest): TroopDetailsDTO =
        troopServiceImpl.addUserInTroop(addUser, request.getAttribute("username") as String)

    @GetMapping("/user")
    fun troopsOfUser(request: HttpServletRequest): List<TroopDetailsDTO> =
        troopServiceImpl.allTroopsofUser(request.getAttribute("username") as String)

}