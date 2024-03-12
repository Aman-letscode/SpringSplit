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
    fun createExpense(@RequestBody expenseDetails: SplitDTO):ResponseEntity<Any> =
         ResponseEntity.ok(paymentServiceImpl.createExpense(expenseDetails))


    @PostMapping("/user")
    fun paymentsOfUser(@RequestBody user: UserDTO):ResponseEntity<Any> = ResponseEntity.ok(paymentServiceImpl.showExpense(user))



    @GetMapping("/user")
    fun expenseDetails(@RequestBody credentials: LoginDTO): ResponseEntity<ExpenseDetails> = ResponseEntity.ok(paymentServiceImpl.expenseDetails(credentials))


    @PutMapping("/paydue")
    fun payDue(@RequestBody paydue: PayDue): ResponseEntity<Any> {
        try {
            val response = paymentServiceImpl.payDue(paydue)
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment")
        }
    }


}