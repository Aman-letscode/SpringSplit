package com.springkotlin.springsplit.email


interface EmailService {
    fun sendSimpleMail(details: EmailDetails):Boolean
}