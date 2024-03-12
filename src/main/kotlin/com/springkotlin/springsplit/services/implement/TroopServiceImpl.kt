package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.Impl.TroopRepositoryImp
import com.springkotlin.springsplit.repositories.PaymentRepository
import com.springkotlin.springsplit.repositories.TroopRepository
import com.springkotlin.springsplit.repositories.UserRepository

import com.springkotlin.springsplit.services.TroopService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.util.DuplicateFormatFlagsException

@Service
class TroopServiceImpl
    (@Autowired var troopRepository: TroopRepository,
                       @Autowired var userRepository: UserRepository
    ): TroopService {

        @Autowired lateinit var paymentRepository: PaymentRepository;

    @Autowired lateinit var troopRepositoryImp: TroopRepositoryImp


    override fun createTroop(troopInfo: AddUserDTO): String {
        val userList= mutableSetOf<User>()
        for (userEmail in troopInfo.emailList) {
            var troopMember: User? = userRepository.findByEmail(userEmail)

            println(troopMember)
            if(troopMember==null) return userEmail+" Not Found "
            userList.add(troopMember!!)
        }
        if(userList.isEmpty()) {
            return "No user added in troop"
        }

        var troop: Troop = Troop(name=troopInfo.troopName, users = userList, (0).toFloat())
        if(troop.users.isEmpty()) return "User not added"
        var result: Troop = troopRepository.save(troop)

        return result.toString()
    }

    override fun allTroopsofUser(userEmailDTO: UserEmailDTO): List<Any> {

        val userDetails:User = userRepository.findByEmail(userEmailDTO.email)?:return arrayListOf("User Not Found")
        val troopList:List<TroopDetailsDTO> = allTroops().filter { it -> it.userList.contains(UserToUserEmailDTO(userDetails)) }

        return troopList
    }

    override fun allTroops():List<TroopDetailsDTO> {
//    override fun allTroops():List<TroopDetailsDTO> {
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



//    fun findByCriteria():List<Troop> = troopRepositoryImp.findAllByTroops()

    override fun addUserInTroop(members: List<String>, troopName: String): String {

        var user_list= mutableSetOf<User>()
        var troop: Troop

        try{

       troop = troopRepository.findByName(troopName)?:return "Troop Not found"
}catch (e:EmptyResultDataAccessException){
    return "Troop Not Found"
}


        val troopDetails:TroopDetailsDTO = allTroops().filter { it.name==troopName }.get(0)
        var memberUser: List<User> = members.mapNotNull { userRepository.findByEmail(it) }
println(memberUser)
        if(memberUser.isEmpty()) return members.joinToString(", ") { "$it: No user found" }
        val membersNotFound = memberUser.filterNot { userEmail -> memberUser.any{it.email==userEmail.email} }
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

    override fun allPaymentsOfTroop(troopName: String): List<PaymentDTO> {
        TODO("Not yet implemented")
    }

    override fun allUnpaidPaymentsofTroops(troop: TroopDTO): List<PaymentDTO> {
        TODO("Not yet implemented")
    }





}