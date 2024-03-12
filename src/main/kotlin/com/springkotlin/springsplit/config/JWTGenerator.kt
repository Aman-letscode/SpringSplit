package com.springkotlin.springsplit.config

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.WeakKeyException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JWTGenerator {

    final var securityConstants:SecurityConstants = SecurityConstants()

    val secretKey:SecretKey = Keys.hmacShaKeyFor(securityConstants.JWT_SECRET.toByteArray())
        .also { key ->
            if (key.encoded.size * 8 < 256) {
                throw WeakKeyException("Key size is not secure enough for HMAC-SHA algorithm.")
            }
        }

    fun generateToken(username: String):String{
//        val username:String = authentication.name
        val currentDate: Date = Date()
        val expiryDate:Date = Date(currentDate.time+securityConstants.JWT_EXPIRATION*1000)
        val token:String = Jwts.builder()
            .claims()
            .subject(username)
            .issuedAt(Date())
            .expiration(expiryDate)
            .and()
            .signWith(secretKey)
            .compact()
return token
    }

    fun isExpired(token:String):Boolean =
        getAllClaims(token)
            .expiration
            .before(Date(System.currentTimeMillis()))


    fun generateUserNameByJWT(token:String):String = getAllClaims(token).subject

    fun validateToken(token:String):Boolean{
        if(isExpired(token)) return false
        else return true
    }


    private fun getAllClaims(token:String): Claims {
        val parser = Jwts.parser()
            .verifyWith(secretKey)
            .build()

        return parser
            .parseSignedClaims(token)
            .payload
    }
}