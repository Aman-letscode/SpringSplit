package com.springkotlin.springsplit.controllers

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Payment
import com.springkotlin.springsplit.services.implement.PaymentServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/payment")
class PaymentController(@Autowired var paymentServiceImpl: PaymentServiceImpl) {
    @GetMapping("/createxpense")
    fun create():String{
        return "Welcome to creation of Expense"
    }

    @PostMapping("/createxpense")
    fun createExpense(@RequestBody expenseDetails: SplitDTO,@RequestHeader("Authorization") token: String):ResponseEntity<Any> =
         ResponseEntity.ok(paymentServiceImpl.createExpense(expenseDetails,token))


    @GetMapping("/user")
    fun paymentsOfUser(@RequestHeader("Authorization") token: String):ResponseEntity<Any> = ResponseEntity.ok(paymentServiceImpl.paymentsOfUser(token))

    @GetMapping("/{Id}")
    fun paymentDetailsOfSplitId(@PathVariable("Id") splitId:String,@RequestHeader("Authorization") token:String):ResponseEntity<SplitStatus> = ResponseEntity.ok(paymentServiceImpl.splitStatus(splitId,token))


    @GetMapping("/summary")
    fun expenseDetails(@RequestHeader("Authorization") token: String): ResponseEntity<ExpenseDetails> = ResponseEntity.ok(paymentServiceImpl.expenseDetails(token))



    @PutMapping("/paydue")
    fun payDue(@RequestBody paydue: PayDue,@RequestHeader("Authorization") token:String): ResponseEntity<Any> {
        try {
            val response = paymentServiceImpl.payDue(paydue,token)
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment")
        }
    }


    @DeleteMapping("/deleteSplit/{splitId}")
    fun deleteSplitPayment(@PathVariable("splitId") splitId: String,@RequestHeader("Authorization") token:String):Any = paymentServiceImpl.deleteTheSplit(splitId,token)




}