package com.springkotlin.springsplit.repositories.Impl

import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import org.springframework.data.jpa.domain.Specification
import jakarta.persistence.criteria.JoinType

object TroopSpecifications {
    fun troopsWithUsers(): Specification<Troop> {
        return Specification { root, _, criteriaBuilder ->
            // Joining the "users" collection with Troop entity via the mapping table
            // Joining the "users" collection with Troop entity via the association
            val joinUsers = root.join<Troop, Set<User>>("users", JoinType.LEFT)
            // Fetching the "troops" collection from the associated User entity
            joinUsers.join<User, Set<Troop>>("troops", JoinType.LEFT)
            // Using distinct to avoid duplication of Troop entities
            criteriaBuilder.and()  // This ensures that there is at least one condition
        }
    }
}
