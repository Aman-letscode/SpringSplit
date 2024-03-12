//package com.springkotlin.springsplit.services.implement
//
//import com.springkotlin.springsplit.config.JWTConfig
//import io.jsonwebtoken.Claims
//import io.jsonwebtoken.Jwts
//import io.jsonwebtoken.security.Keys
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.stereotype.Service
//import java.util.*
//@Service
//class JWTService(
//    jwtConfig: JWTConfig
//) {
//
//    private val secretKey = Keys.hmacShaKeyFor(
//        jwtConfig.key.toByteArray()
//    )
//
//    fun generate(
//        userDetails:UserDetails,
//        expirationDate: Date,
//        additionalClaims:Map<String,Any> = emptyMap()
//    ):String =
//        Jwts.builder()
//            .claims()
//            .subject(userDetails.username)
//            .issuedAt(Date(System.currentTimeMillis()))
//            .expiration(expirationDate)
//            .add(additionalClaims)
//            .and()
//            .signWith(secretKey)
//            .compact()
//
//    fun extractEmail(token:String):String? =
//        getAllClaims(token)
//            .subject
//
//    fun isExpired(token:String):Boolean =
//        getAllClaims(token)
//            .expiration
//            .before(Date(System.currentTimeMillis()))
//
//    fun isValid(token: String,userDetails: UserDetails):Boolean{
//        val email = extractEmail(token)
//        return userDetails.username == email && !isExpired(token)
//
//    }
//    private fun getAllClaims(token:String): Claims {
//        val parser = Jwts.parser()
//            .verifyWith(secretKey)
//            .build()
//
//        return parser
//            .parseSignedClaims(token)
//            .payload
//    }
//}