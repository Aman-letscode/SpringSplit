package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.dto.ExpenseDTO
import com.springkotlin.springsplit.dto.PayDue
import com.springkotlin.springsplit.dto.UserDTO
import com.springkotlin.springsplit.entities.Payment
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.PaymentRepository
import com.springkotlin.springsplit.repositories.TroopRepository
import com.springkotlin.springsplit.repositories.UserRepository
import com.springkotlin.springsplit.services.PaymentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import java.math.RoundingMode

@Service
class PaymentServiceImpl:PaymentService {
    @Autowired lateinit var paymentRepository: PaymentRepository

    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var troopRepository: TroopRepository
    override fun createExpense(expenseDTO: ExpenseDTO): List<Payment> {


        var refunderList:List<Int> = expenseDTO.splitList

        println(refunderList.size)

        println(troopRepository.findById(expenseDTO.troopId).get())
        if(refunderList.isEmpty()){
            refunderList = troopRepository.findById(expenseDTO.troopId).get().users.toList().map { it.id }
        }
        print(refunderList)
        var amountPerMember:Float = ((expenseDTO.amount).toFloat()/(refunderList.size+1)).toBigDecimal().setScale(2,RoundingMode.CEILING).toFloat()

        val receiver:User = userRepository.findById(expenseDTO.userId).get()

        val troop: Troop = troopRepository.findById(expenseDTO.troopId).get()

        val paymentList = mutableListOf<Payment>()
        for(userId in refunderList){
            val refunder:User = userRepository.findById(userId).get()
            val payDue:Payment = Payment(amount = amountPerMember, refunder = refunder, receiver = receiver, troop = troop, status = "unpaid")

            val saved = paymentRepository.save(payDue)
            paymentList.add(saved)
        }
        val receiverPayment:Payment = Payment(amountPerMember,receiver,receiver,troop,"paid")

//        val saved = paymentRepository.save(receiverPayment)
//        paymentList.add(saved)

//        return ExpenseDTO(10,1, mutableListOf(1,2,3,5),3)
return paymentList
    }

    override fun showExpense(user: UserDTO): List<Payment> {

        val userData: User = userRepository.findByEmail(user.email)?:throw NotFoundException()

        var paymentList:List<Payment> = mutableListOf<Payment>()

        var paymentAsRefunder: List<Payment> = paymentRepository.findByRefunder(userData)
        var paymentAsReceiver: List<Payment> = paymentRepository.findByReceiver(userData)

        paymentList = (paymentAsRefunder + paymentAsReceiver)
        return paymentList

    }

    override fun payDue(payDue: PayDue): Payment {
        val userData: User = userRepository.findByEmail(payDue.email)?:throw NotFoundException()
        var paymentCard:Payment = paymentRepository.findById(payDue.paymentId).get()
        paymentCard.status= "paid"
        val result:Payment = paymentRepository.save(paymentCard)
        return result

    }


}