package com.springkotlin.springsplit.controllers

import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.services.implement.PaymentServiceImpl
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/payment")
class PaymentController(@Autowired var paymentServiceImpl: PaymentServiceImpl) {
    @GetMapping("/createxpense")
    fun create(): String {
        return "Welcome to creation of Expense"
    }

    @PostMapping("/createxpense")
    fun createExpense(
        @RequestBody expenseDetails: SplitDTO,
        request: HttpServletRequest,
    ): ResponseEntity<List<PaymentDTO>> =
        ResponseEntity.ok(paymentServiceImpl.createExpense(expenseDetails, request.getAttribute("username") as String))


    @GetMapping("/user")
    fun paymentsOfUser(@RequestBody memberType: MemberType, request: HttpServletRequest): ResponseEntity<List<PaymentDTO>> =
        ResponseEntity.ok(paymentServiceImpl.paymentsOfUser(memberType, request.getAttribute("username") as String))

    @GetMapping("/{Id}")
    fun paymentDetailsOfSplitId(
        @PathVariable("Id") splitId: String,
        request: HttpServletRequest,
    ): ResponseEntity<SplitStatus> =
        ResponseEntity.ok(paymentServiceImpl.splitStatus(splitId, request.getAttribute("username") as String))


    @GetMapping("/summary")
    fun expenseDetails(request: HttpServletRequest): ResponseEntity<ExpenseDetails> =
        ResponseEntity.ok(paymentServiceImpl.expenseDetails(request.getAttribute("username") as String))


    @PostMapping("/sendreminder")
    fun sendReminderForPayment(
        @RequestBody reminderRequest: ReminderRequest,
        request: HttpServletRequest,
    ): ResponseEntity<ReminderResponse> =
        ResponseEntity.ok(paymentServiceImpl.sendReminder(reminderRequest, request.getAttribute("username") as String))

    @PutMapping("/paydue")
    fun payDue(@RequestBody paydue: PayDue, request: HttpServletRequest): ResponseEntity<PaymentDTO> =
        ResponseEntity.ok(paymentServiceImpl.payDue(paydue, request.getAttribute("username") as String))


    @DeleteMapping("/deleteSplit/{Id}")
    fun deleteSplitPayment(
        @PathVariable("Id") splitId: String,
        request: HttpServletRequest,
    ): ResponseEntity<List<PaymentDTO>> =
        ResponseEntity.ok(paymentServiceImpl.deleteTheSplit(splitId, request.getAttribute("username") as String))


//    @GetMapping("/fruits")
//    fun returnFruits():List<String> = paymentServiceImpl.getList()



}