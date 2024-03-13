package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.config.JWTGenerator
import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.Impl.TroopSpecifications
import com.springkotlin.springsplit.repositories.PaymentRepository
import com.springkotlin.springsplit.repositories.TroopRepository
import com.springkotlin.springsplit.repositories.UserRepository
import com.springkotlin.springsplit.services.TroopService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

@Service
class TroopServiceImpl
    (
    @Autowired var troopRepository: TroopRepository,
    @Autowired var userRepository: UserRepository,
) : TroopService {

    val jwtGenerator: JWTGenerator = JWTGenerator()


    override fun createTroop(troopInfo: AddUserDTO, token: String): String {
        val user: User = extractUserFromToken(token)
        val userList = mutableSetOf<User>()
        val userNotFound = mutableSetOf<String>()
        if(troopRepository.existsByName(troopInfo.troopName)) return "${troopInfo.troopName} already exist"
        userList.add(user)
        for (userEmail in troopInfo.emailList) {
            if(userEmail!=user.email){

            val troopMember: User? = userRepository.findByEmail(userEmail)
            if (troopMember == null) userNotFound.add(userEmail)
            else {
                if(troopMember.role.name=="ADMIN")  return "$userEmail is not an user"
                userList.add(troopMember)
            }
            }
        }
        if (userNotFound.isNotEmpty()) return userNotFound.joinToString(",") { " not found" }
        if (userList.isEmpty()) {
            return "No user added in troop"
        }
        var troop: Troop = Troop(name = troopInfo.troopName, users = userList, (0).toFloat())
        if (troop.users.isEmpty()) return "User not added"
        var result: Troop = troopRepository.save(troop)
        return TroopToTroopDetailsDTO(result).toString()
    }

    override fun allTroopsofUser(userEmailDTO: UserEmailDTO, token: String): List<Any> =
        allTroops().filter { it -> it.userList.contains(UserToUserEmailDTO(extractUserFromToken(token))) }


    override fun allTroops(): List<TroopDetailsDTO> {
        val result = troopRepository.findTroops()
        val troopDetailsMap = mutableMapOf<Int, TroopDetailsDTO>()

        for (array in result) {
            val troopId = array[1] as Int
            val troopName = array[3] as String
            val userId = array[0] as Int

            val user = userRepository.findById(userId).orElse(null)

            val troopDetails = troopDetailsMap.getOrPut(troopId) {
                TroopDetailsDTO(name = troopName, userList = mutableSetOf(), totalAmountTransacted = 0F)
            }

            troopDetails.userList.add(UserToUserEmailDTO(user))
        }


        return troopDetailsMap.values.toList()
    }
//    fun allTroopsBySpec():List<Troop> = troopRepository.findAll(TroopSpecifications.troopsWithUsers())


    override fun addUserInTroop(addUserDTO: AddUserDTO, token: String): String {


        val troop: Troop
        try {
            troop = troopRepository.findByName(addUserDTO.troopName)
        } catch (e: EmptyResultDataAccessException) {
            return "Troop Not Found"
        }
        if (!troopRepository.existsByNameAndUsers(
                addUserDTO.troopName,
                extractUserFromToken(token)
            )
        ) return "User Not Part of the troop"
        val troopDetails: TroopDetailsDTO = allTroops().filter { it.name == addUserDTO.troopName }.get(0)


        var memberUser: List<User> = addUserDTO.emailList.mapNotNull { userRepository.findByEmail(it) }
        //user Not found
        if (memberUser.isEmpty()) return addUserDTO.emailList.joinToString(", ") { "$it: No user found" }
        val membersNotFound = memberUser.filterNot { userEmail -> memberUser.any { it.email == userEmail.email } }
        if (membersNotFound.isNotEmpty()) {
            return membersNotFound.joinToString(", ") { "$it: No user found" }
        }


        memberUser = memberUser.filter { UserToUserEmailDTO(it) !in troopDetails.userList }
        val updatedMembers: MutableSet<User> = troop.users.plus(memberUser).toMutableSet()

        val updatedTroop: Troop = troop.copy(users = updatedMembers)

        val savedTroop: Troop = troopRepository.save(updatedTroop)

        return if (savedTroop.id > 0) {
            "Members added to the troop successfully"
        } else {
            "Failed to update troop"
        }
    }


    private fun extractUserFromToken(token: String): User =
        userRepository.findByEmail(jwtGenerator.generateUserNameByJWT(token.substring(7, token.length)))!!


}