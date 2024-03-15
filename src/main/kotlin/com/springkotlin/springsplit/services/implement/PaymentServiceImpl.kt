package com.springkotlin.springsplit.services.implement


import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.email.EmailDetails
import com.springkotlin.springsplit.email.EmailServiceImpl
import com.springkotlin.springsplit.entities.Payment
import com.springkotlin.springsplit.entities.Troop
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.PaymentRepository
import com.springkotlin.springsplit.services.EntityFunctions
import com.springkotlin.springsplit.services.PaymentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import java.math.RoundingMode

@Service
class PaymentServiceImpl : PaymentService {

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    @Autowired
    private lateinit var emailServiceImpl: EmailServiceImpl

    @Autowired
    private lateinit var entityFunctions: EntityFunctions

    override fun createExpense(splitDTO: SplitDTO, username: String): List<PaymentDTO> {
        if (splitDTO.amount <= 0) throw Exception("Amount cannot be less than or equal to 0")
        if (splitDTO.troopName == "") throw Exception("Enter User Troop Name")
        val receiver: User = entityFunctions.findUserByEmail(username)

        entityFunctions.validateUsersInTroop(splitDTO.troopName, receiver)

        val refunderList: List<String> = entityFunctions.getRefunderEmails(splitDTO, username)
            .toMutableSet()
            .toList()
        val amountPerMember = calculateAmount(splitDTO.amount, refunderList.size)
        val troop: Troop = entityFunctions.findTroopByName(splitDTO.troopName)
        val splitId: String = generateSplitId()

        return refunderList.map { userEmail ->
            val refunder: User = entityFunctions.findUserByEmail(userEmail)
            entityFunctions.validateUsersInTroop(splitDTO.troopName, refunder)
            Payment(
                splitId = splitId,
                amount = amountPerMember,
                refunder = refunder,
                receiver = receiver,
                troop = troop,
                status = "unpaid"
            )
        }.let { payments ->
            paymentRepository.saveAll(payments)
        }.map { PaymentToPaymentDTO(it) }

    }


    override fun paymentsOfUser(memberType: MemberType, username: String): List<PaymentDTO> {
        val userData: User = entityFunctions.findUserByEmail(username)
        return when (memberType.type) {
            "refunder" -> paymentRepository.findByRefunder(userData).map { PaymentToPaymentDTO(it) }
            "receiver" -> paymentRepository.findByReceiver(userData).map { PaymentToPaymentDTO(it) }
            else -> {
                val paymentAsRefunder = paymentRepository.findByRefunder(userData).map { PaymentToPaymentDTO(it) }
                val paymentAsReceiver = paymentRepository.findByReceiver(userData).map { PaymentToPaymentDTO(it) }
                (paymentAsRefunder + paymentAsReceiver)
            }
        }
    }


    override fun payDue(payDue: PayDue, username: String): PaymentDTO {
        val user = entityFunctions.findUserByEmail(username)
        val paymentCard = paymentRepository.findBySplitIdAndRefunder(payDue.splitId, user)[0]
        if (paymentCard.status == "paid") return PaymentToPaymentDTO(paymentCard)
        if (paymentCard.amount <= payDue.amount) {
            paymentCard.status = "paid"
        } else {
            paymentCard.amount -= payDue.amount
        }
        val result = paymentRepository.save(paymentCard)
        return PaymentToPaymentDTO(result)
    }

    override fun expenseDetails(username: String): ExpenseDetails {
        val user = entityFunctions.findUserByEmail(username)

        val paidPayments = paymentRepository.findByRefunder(user).filter { it.status == "paid" }
        val unpaidPayments = paymentRepository.findByRefunder(user).filter { it.status == "unpaid" }

        val receivedPayments = paymentRepository.findByReceiver(user).filter { it.status == "paid" }
        val leftPayments = paymentRepository.findByReceiver(user).filter { it.status == "unpaid" }

        return ExpenseDetails(
            email = username,
            totalAmountPaid = paidPayments.sumOf { it.amount.toDouble() }.toFloat(),
            totalAmountDue = unpaidPayments.sumOf { it.amount.toDouble() }.toFloat(),
            totalAmountReceived = receivedPayments.sumOf { it.amount.toDouble() }.toFloat(),
            totalAmountLeft = leftPayments.sumOf { it.amount.toDouble() }.toFloat()
        )

    }

    override fun splitStatus(splitId: String, username: String): SplitStatus {
        val paymentDetails: List<Payment> = paymentRepository.findBySplitId(splitId)
        if (userNotIncludeInSplit(paymentDetails, username, false))
            throw Exception("User Not Part Of the split")

        val receiver: UserEmailDTO = UserToUserEmailDTO(paymentDetails.first().receiver!!)
        val amount: Float = paymentDetails.first().amount

        val paidList: List<UserEmailDTO> =
            paymentDetails.filter { it.status == "paid" }
                .map { UserToUserEmailDTO(it.refunder!!) }
        val dueList: List<UserEmailDTO> =
            paymentDetails.filter { it.status == "unpaid" }
                .map { UserToUserEmailDTO(it.refunder!!) }
        return SplitStatus(splitId, receiver, paidList, dueList, amount)
    }

    override fun sendReminder(reminderRequest: ReminderRequest, username: String): ReminderResponse {
        val user = entityFunctions.findUserByEmail(username)

        val paymentReminderList: List<Payment> =
            paymentRepository.findBySplitIdAndReceiver(reminderRequest.splitId, user).filter { it.status == "unpaid" }
        if (paymentReminderList.isEmpty()) throw Exception("No Payment Exist")

        for (reminder in paymentReminderList) {
            val subject: String = reminder.splitId + " Due Reminder!!"
            val message: String =
                "SPLIT: " + reminder.splitId + " is been due\nAmount: " + reminder.amount + "\nPay To: " + reminder.receiver?.email
            val emailDetail = EmailDetails(
                recipient = reminder.refunder?.email ?: throw NotFoundException(),
                subject = subject,
                msgBody = message
            )
            emailServiceImpl.sendSimpleMail(emailDetail)
        }

        return ReminderResponse(reminderRequest.splitId, "All reminders are sent")
    }


    override fun deleteTheSplit(splitId: String, username: String): List<PaymentDTO> {
        val paymentList = paymentRepository.findBySplitId(splitId)
        if (userNotIncludeInSplit(paymentList, username, true))
            throw Exception("You are not the receiver of the split")

        paymentList.filter { it.status == "paid" }
            .forEach {
                paymentRepository.deleteById(it.id)
            }

        val paymentDueByUser = paymentList
            .filter { it.status == "paid" }
            .map { it ->
                it.splitId = generateSplitId()
                val userInterchange = it.refunder
                it.refunder = it.receiver
                it.receiver = userInterchange
                it.status = "unpaid"
                paymentRepository.save(it)
            }

        return paymentDueByUser.map { PaymentToPaymentDTO(it) }
    }

    private fun generateSplitId(): String {
        var splitId: String
        do {
            splitId = "SPLIT" + (0..10000).random()
        } while (paymentRepository.existsBySplitId(splitId))
        return splitId
    }

    private fun calculateAmount(amount: Float, count: Int): Float {
        return amount.div(count + 1)
            .toBigDecimal()
            .setScale(2, RoundingMode.CEILING)
            .toFloat()
    }

    private fun userNotIncludeInSplit(paymentList: List<Payment>, username: String, needToDelete: Boolean): Boolean =
         when (needToDelete) {
            true -> paymentList.filter { it.receiver!!.email.contains(username) }
            false -> paymentList.filter { it.receiver!!.email.contains(username) || it.refunder!!.email.contains(username) }
        }.isEmpty()


}