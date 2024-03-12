package com.springkotlin.springsplit.repositories.Impl

import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.TroopRepository
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.stereotype.Repository
import jakarta.persistence.criteria.*
import org.springframework.beans.factory.annotation.Autowired


@Repository
class TroopRepositoryImp {
    @Autowired lateinit var entityManager: EntityManager
    fun findAllByTroops():List<Troop>{
        val criteriaBuilder: CriteriaBuilder? = entityManager.criteriaBuilder
        val criteriaQuery: CriteriaQuery<Troop> = criteriaBuilder?.createQuery(Troop::class.java)!!

        val troopRoot: Root<Troop> = criteriaQuery.from(Troop::class.java)


        val userJoin = troopRoot.joinSet<Troop,User>("users")

        criteriaQuery.select(troopRoot).distinct(true) // Use distinct to avoid duplicates

        val query = entityManager.createQuery(criteriaQuery)

        return query.resultList
    }
}