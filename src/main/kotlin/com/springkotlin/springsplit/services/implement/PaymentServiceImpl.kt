package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.dto.*
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
class PaymentServiceImpl : PaymentService {
    @Autowired
    lateinit var paymentRepository: PaymentRepository

    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var troopRepository: TroopRepository
    override fun createExpense(splitDTO: SplitDTO): Any {

        var refunderList: List<String> = splitDTO.splitList
        if (refunderList.isEmpty()) {
            refunderList = troopRepository.findByName(splitDTO.troopName).users.toList().map { it.email }
        }
        val splitAmount = ((splitDTO.amount).toFloat() / (refunderList.size + 1))
        val amountPerMember = splitAmount.toBigDecimal().setScale(2, RoundingMode.CEILING).toFloat()

        val receiver: User = userRepository.findByEmail(splitDTO.userEmail)?: return "UserEmail Does not exist"
        val troop: Troop
        if(splitDTO.troopName.equals(""))
        troop = troopRepository.findAll().get(0)
        else  troop = troopRepository.findByName(splitDTO.troopName)

        val paymentList = mutableListOf<PaymentDTO>()
        for (userId in refunderList) {
            val refunder: User = userRepository.findByEmail(userId)!!
            val payDue = Payment(
                amount = amountPerMember,
                refunder = refunder,
                receiver = receiver,
                troop = troop,
                status = "unpaid"
            )

            val savedPayment = paymentRepository.save(payDue)
            val saved = PaymentDTO(
                savedPayment.amount,
                refunder = UserToUserEmailDTO(savedPayment.refunder),
                receiver = UserToUserEmailDTO(savedPayment.receiver),
                troop = TroopToTroopDetailsDTO(savedPayment.troop),
                status = savedPayment.status
            )
            paymentList.add(saved)
        }

        return paymentList
    }

    override fun showExpense(user: UserDTO): Any {
        val userData: User = userRepository.findByEmail(user.email) ?: return "User Not Found"
        var paymentList: List<Payment> = mutableListOf<Payment>()
        var paymentAsRefunder: List<Payment> = paymentRepository.findByRefunder(userData)
        var paymentAsReceiver: List<Payment> = paymentRepository.findByReceiver(userData)
        paymentList = (paymentAsRefunder + paymentAsReceiver)
        return paymentList
    }

    override fun payDue(payDue: PayDue): PaymentDTO {
        userRepository.findByEmail(payDue.email) ?: throw NotFoundException()
        var paymentCard: Payment = paymentRepository.findById(payDue.paymentId).get()
        paymentCard.status = "paid"
        val result: Payment = paymentRepository.save(paymentCard)
        return PaymentToPaymentDTO(result)
    }

    override fun expenseDetails(credentials: LoginDTO): ExpenseDetails {
        val user = userRepository.findByEmail(credentials.email)!!
        var totalAmountPaid = 0F
        var totalAmountDue = 0F
        var totalAmountRecieve = 0F
        var totalAmountLeft = 0F

        var paymentList:List<Payment> = paymentRepository.findByRefunder(user).filter { it.status=="paid" }
        totalAmountPaid = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = paymentRepository.findByRefunder(user).filter { it.status == "unpaid" }
        totalAmountDue = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = paymentRepository.findByReceiver(user).filter { it.status == "paid" }
        totalAmountRecieve = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = paymentRepository.findByReceiver(user).filter { it.status == "unpaid" }
        totalAmountLeft = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        return ExpenseDetails(
                credentials.email,
                totalAmountPaid,
                totalAmountDue,
                totalAmountRecieve,
                totalAmountLeft
        )

    }


}