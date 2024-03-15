package com.springkotlin.springsplit.services

import com.springkotlin.springsplit.dto.SplitDTO
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.TroopRepository
import com.springkotlin.springsplit.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Component
import java.nio.file.FileAlreadyExistsException

@Component
class EntityFunctions {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var troopRepository: TroopRepository


    fun validateUsersInTroop(troopName: String, user: User) {
        if (!troopRepository.existsByNameAndUsers(troopName, user)) {
            throw IllegalArgumentException("${user.email} is not in the troop: $troopName")
        }
    }

    fun findTroopByName(troopName: String): Troop {
        return troopRepository.findByName(troopName) ?: throw NotFoundException()

    }

    fun findUserByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw NotFoundException()
    }

    fun getRefunderEmails(splitDTO: SplitDTO, requestingUserEmail: String): List<String> {
        return if (splitDTO.splitList.isEmpty()) {
            troopRepository.findByName(splitDTO.troopName)?.users?.map { it.email }
                ?.filter { it != requestingUserEmail } ?: throw NotFoundException()
        } else {
            splitDTO.splitList.filter { it != requestingUserEmail }
        }
    }

    fun validateTroopExistance(troopName: String) {
        if (troopRepository.existsByName(troopName))
            throw FileAlreadyExistsException("$troopName already exist")

    }

    fun checkEmail(email: String): Boolean {
        val regex = """^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$""".toRegex()
        return regex.matches(email)
    }

    fun validateUserListAndTroop(userList: MutableSet<User>) {
        var alreadyATroop = troopRepository.findAll()
        userList.map {
            alreadyATroop = alreadyATroop.intersect(troopRepository.findByUsers(it).toSet()).toMutableList()
        }
        if(alreadyATroop.size ==1) throw Exception("Troop already Present")
    }

}