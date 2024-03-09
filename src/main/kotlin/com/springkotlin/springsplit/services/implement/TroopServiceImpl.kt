package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.dto.AddUserDTO
import com.springkotlin.springsplit.dto.PaymentDTO
import com.springkotlin.springsplit.dto.TroopDTO
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.PaymentRepository
import com.springkotlin.springsplit.repositories.TroopRepository
import com.springkotlin.springsplit.repositories.UserRepository
import com.springkotlin.springsplit.services.TroopService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TroopServiceImpl
    (@Autowired var troopRepository: TroopRepository,
                       @Autowired var userRepository: UserRepository
    ): TroopService {

        @Autowired lateinit var paymentRepository: PaymentRepository;
    override fun createTroop(troopInfo: AddUserDTO): String {
        val userList= mutableSetOf<User>()
        for (userEmail in troopInfo.emailList) {
            var troopMember: User = userRepository.findByEmail(userEmail)
            userList.add(troopMember)
        }
        if(userList.isEmpty()) {
            return "No user added in troop"
        }
        var troop: Troop = Troop(name=troopInfo.troopName, users = userList, (0).toFloat())
        var result: Troop = troopRepository.save(troop)

        return result.toString()
    }

    override fun allTroopsofUser(userId: Int): List<TroopDTO> {

        val userDetails:User = userRepository.findById(userId).get()

        val troopList:List<Troop> = troopRepository.findByUsers(userDetails)

        var result = mutableListOf<TroopDTO>()
        for(troops in troopList){
            val troopDto:TroopDTO = TroopDTO(troops.name,troops.users,troops.totalAmount)
            result.add(troopDto)
        }
        return result.toList()

    }

    override fun allTroops(): List<TroopDTO> = troopRepository.findAll()
                .map { it ->
                    TroopDTO(
                        name = it.name,
                        user_list = it.users,
                        totalAmountTransacted = (0).toFloat()
                    )
                }



//        var result = mutableListOf<TroopDTO>()
//        for(troops in troopList){
//            println(troops.users)
//            val troopDto:TroopDTO = TroopDTO(troops.name,troops.users.toList(),troops.totalAmount)
////            println(troopDto)
//            result.add(troopDto)
//        }
//        return troopList


    override fun addUserInTroop(members: List<String>, troopName: String): String {
//        TODO("Not yet implemented")
        var user_list= mutableSetOf<User>()


        val troop: Troop = troopRepository.findByName(troopName)
        if(troop==null) return "Troop Not found"

        val memberUser: List<User> = members.map { userRepository.findByEmail(it)!! }

        val membersNotFound = memberUser.filter { it == null }
        if (membersNotFound.isNotEmpty()) {
            return membersNotFound.joinToString(", ") { "$it: No user found" }
        }

        val updatedMembers: MutableSet<User> = troop.users.plus(memberUser.filterNotNull()).toMutableSet()

        val updatedTroop: Troop = troop.copy(users = updatedMembers)

        val savedTroop: Troop = troopRepository.save(updatedTroop)

        return if (savedTroop.id > 0) {
            "Members added to the troop successfully"
        } else {
            "Failed to update troop"
        }

    }

    override fun allPaymentsOfTroop(troopName: String): List<PaymentDTO> =
        paymentRepository.findByTroop(troopRepository.findByName(troopName))
            .map { it-> PaymentDTO(
                amount = it.amount,
                refunder = it.refunder,
                receiver = it.receiver,
                troop= it.troop,
                status = it.status
        ) }

    override fun allUnpaidPaymentsofTroops(troop: TroopDTO): List<PaymentDTO> {
        TODO("Not yet implemented")
    }
}