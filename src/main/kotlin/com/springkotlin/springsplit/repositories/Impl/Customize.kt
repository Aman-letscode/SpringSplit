//package com.springkotlin.springsplit.repositories.Impl
//
//import com.fasterxml.jackson.annotation.JsonGetter
//import com.springkotlin.springsplit.entities.Troop
//import com.springkotlin.springsplit.entities.User
//import org.springframework.stereotype.Repository
//import javax.persistence.EntityManager
//import javax.persistence.Persistence
//import javax.persistence.criteria.CriteriaBuilder
//import javax.persistence.criteria.CriteriaQuery
//import javax.persistence.criteria.Root
//import javax.persistence.criteria.Join
//
//
//@Repository
//class Customize{
//
//    val  entityManager: EntityManager = Persistence.createEntityManagerFactory("my-persistence-unit").createEntityManager()
//
//    fun findAllTroopsWithUsers(): List<Troop> {
//        val criteriaBuilder: CriteriaBuilder = entityManager.criteriaBuilder
//        val criteriaQuery: CriteriaQuery<Troop> = criteriaBuilder.createQuery(Troop::class.java)
//
//        val troopRoot: Root<Troop> = criteriaQuery.from(Troop::class.java)
//
//        val userJoin: Join<Troop, User> = troopRoot.join("user")
//
//        criteriaQuery.select(troopRoot).distinct(true) // Use distinct to avoid duplicates
//
//        val query = entityManager.createQuery(criteriaQuery)
//
//        return query.resultList
//    }
//
//
//}
