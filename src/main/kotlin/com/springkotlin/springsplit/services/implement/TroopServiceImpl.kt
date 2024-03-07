package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.dto.TroopDTO
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.TroopRepository
import com.springkotlin.springsplit.repositories.UserRepository
import com.springkotlin.springsplit.services.TroopService
import org.hibernate.annotations.NotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashSet

@Service
class TroopServiceImpl
    (@Autowired var troopRepository: TroopRepository,
                       @Autowired var userRepository: UserRepository
    ): TroopService {
    override fun createTroop(troopInfo: TroopDTO): String {
        var user_list= mutableSetOf<User>()
        for (email in troopInfo.user_list){
            var troopMember: User? = userRepository.findByEmail(email)
            if(troopMember==null){
                return email+": No user found"
            }
            user_list.add(troopMember)
        }

        if(user_list.isEmpty()) {
            return "No user added in troop"
        }
        var owner:User = userRepository.findByEmail(troopInfo.createdBy)?:throw NotFoundException()
        user_list.add(owner)

        var troop: Troop = Troop(name=troopInfo.name, users = HashSet(), totalAmount = 0)

        troop.users = user_list
        var result: Troop = troopRepository.save(troop)

//        result.users = user_list

        return if (result==null)"Troop not created" else "Troop Created Successfully"
    }

    override fun allTroopsofUser(user: User):List<Troop> {

        return troopRepository.findAll()

    }

    override fun allTroops(): List<Troop> {
        return troopRepository.findAll()
    }

    override fun addUserInTroop(members: List<String>, troopName: String): String {
//        TODO("Not yet implemented")
        var user_list= HashSet<User>()


        val troop: Troop = troopRepository.findByName(troopName)
        if(troop==null) return "Troop Not found"

        val memberUser: List<User> = members.map { userRepository.findByEmail(it)!! }

        val membersNotFound = memberUser.filter { it == null }
        if (membersNotFound.isNotEmpty()) {
            return membersNotFound.joinToString(", ") { "$it: No user found" }
        }

        val updatedMembers: Set<User> = troop.users.plus(memberUser.filterNotNull())

        val updatedTroop: Troop = troop.copy(users = updatedMembers)

        val savedTroop: Troop = troopRepository.save(updatedTroop)

        return if (savedTroop.id > 0) {
            "Members added to the troop successfully"
        } else {
            "Failed to update troop"
        }

    }
}