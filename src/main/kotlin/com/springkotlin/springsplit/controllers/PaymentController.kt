package com.springkotlin.springsplit.controllers

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.entities.Payment
import com.springkotlin.springsplit.services.implement.PaymentServiceImpl
import jakarta.servlet.http.HttpServletRequest
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
    fun createExpense(@RequestBody expenseDetails: SplitDTO,request:HttpServletRequest):ResponseEntity<Any> =
         ResponseEntity.ok(paymentServiceImpl.createExpense(expenseDetails,request.getAttribute("username") as String))


    @GetMapping("/user")
    fun paymentsOfUser(@RequestBody userAs: UserAs,request:HttpServletRequest):ResponseEntity<Any> =
        ResponseEntity.ok(paymentServiceImpl.paymentsOfUser(userAs,request.getAttribute("username") as String))

    @GetMapping("/{Id}")
    fun paymentDetailsOfSplitId(@PathVariable("Id") splitId:String,request:HttpServletRequest):ResponseEntity<SplitStatus> =
        ResponseEntity.ok(paymentServiceImpl.splitStatus(splitId,request.getAttribute("username") as String))


    @GetMapping("/summary")
    fun expenseDetails(request:HttpServletRequest): ResponseEntity<ExpenseDetails> =
        ResponseEntity.ok(paymentServiceImpl.expenseDetails(request.getAttribute("username") as String))


    @PostMapping("/sendreminder/{splitId}")
    fun sendReminderForPayment(@PathVariable("splitId") splitId: String ,request: HttpServletRequest):ResponseEntity<String> =
        ResponseEntity.ok(paymentServiceImpl.sendReminder(splitId,request.getAttribute("username") as String))

    @PutMapping("/paydue")
    fun payDue(@RequestBody paydue: PayDue,request:HttpServletRequest): ResponseEntity<Any> {
        try {
            val response = paymentServiceImpl.payDue(paydue,request.getAttribute("username") as String)
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment")
        }
    }



    @DeleteMapping("/deleteSplit/{splitId}")
    fun deleteSplitPayment(@PathVariable("splitId") splitId: String,request:HttpServletRequest):ResponseEntity<Any> =
        ResponseEntity.ok(paymentServiceImpl.deleteTheSplit(splitId,request.getAttribute("username") as String))






}