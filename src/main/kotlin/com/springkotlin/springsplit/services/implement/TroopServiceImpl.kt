package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.TroopRepository
import com.springkotlin.springsplit.repositories.UserRepository
import com.springkotlin.springsplit.services.EntityFunctions
import com.springkotlin.springsplit.services.TroopService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TroopServiceImpl
    (
    @Autowired private var troopRepository: TroopRepository,
    @Autowired private var userRepository: UserRepository,
) : TroopService {

    @Autowired
    private lateinit var entityFunctions: EntityFunctions
    override fun createTroop(troopInfo: AddUserDTO, username: String): TroopDetailsDTO {
        entityFunctions.validateTroopExistance(troopInfo.troopName)
        val user: User = entityFunctions.findUserByEmail(username)
        val userList = mutableSetOf<User>()
        userList.add(user)

        for (userEmail in troopInfo.emailList) {
            if (userEmail != user.email) {
                val troopMember: User = entityFunctions.findUserByEmail(userEmail)
                if (troopMember.role.name == "ADMIN") throw Exception("$userEmail is not an user")
                userList.add(troopMember)
            }
        }

        val troop = Troop(name = troopInfo.troopName, users = userList, 0F)
        val result: Troop = troopRepository.save(troop)
        return TroopToTroopDetailsDTO(result)
    }


    override fun allTroops(): List<TroopDetailsDTO> {
        val result = troopRepository.findTroops()
        val troopDetailsMap = mutableMapOf<Int, TroopDetailsDTO>()

        for (array in result) {
            val troopId = array[1] as Int
            val troopName = array[3] as String
            val userId = array[0] as Int
            val user = userRepository.findById(userId).get()
            val troopDetails = troopDetailsMap.getOrPut(troopId) {
                TroopDetailsDTO(name = troopName, userList = mutableSetOf(), totalAmountTransacted = 0F)
            }
            troopDetails.userList.add(UserToUserEmailDTO(user))
        }

        return troopDetailsMap.values.toList()
    }

    override fun allTroopsofUser(username: String): List<TroopDetailsDTO> =
        allTroops().filter {
            it.userList.contains(UserToUserEmailDTO(entityFunctions.findUserByEmail(username)))
        }.sortedBy{it.name}


    override fun addUserInTroop(addUserDTO: AddUserDTO, username: String): TroopDetailsDTO {
        if (addUserDTO.emailList.isEmpty()) throw Exception("No user requested to add")
        val user: User = entityFunctions.findUserByEmail(username)
        val troop = entityFunctions.findTroopByName(addUserDTO.troopName)
        entityFunctions.validateUsersInTroop(addUserDTO.troopName, user)


        val troopDetails: TroopDetailsDTO = allTroops().filter { it.name == addUserDTO.troopName }[0]
        var memberUser: List<User> = addUserDTO.emailList.map { entityFunctions.findUserByEmail(it) }
        memberUser = memberUser.filter { UserToUserEmailDTO(it) !in troopDetails.userList }

        val updatedMembers: MutableSet<User> = troop.users.plus(memberUser).toMutableSet()
        val updatedTroop: Troop = troop.copy(users = updatedMembers)


        val savedTroop: Troop = troopRepository.save(updatedTroop)

        return TroopToTroopDetailsDTO(savedTroop)
    }


}