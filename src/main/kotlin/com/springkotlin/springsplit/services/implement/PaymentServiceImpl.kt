package com.springkotlin.springsplit.services.implement


import com.springkotlin.springsplit.dto.*
import com.springkotlin.springsplit.email.EmailDetails
import com.springkotlin.springsplit.email.EmailServiceImpl
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

    @Autowired
    lateinit var emailServiceImpl: EmailServiceImpl


    override fun createExpense(splitDTO: SplitDTO,username:String): String {
        if(splitDTO.amount<=0) return "Amount cannot be less than or equal to 0"
        val user:User = userRepository.findByEmail(username)!!
        var refunderList: List<String> = splitDTO.splitList.filter { it!=username }
        if(splitDTO.troopName == "") return "Enter User Troop Name"
        if(!troopRepository.existsByNameAndUsers(splitDTO.troopName,user)) return username + " is not part of the Troop: "+ splitDTO.troopName
        if (refunderList.isEmpty()) {
            refunderList = troopRepository.findByName(splitDTO.troopName).users.toList().map { it.email }.filter { it != username }
        }
        val splitAmount = ((splitDTO.amount).toFloat() / (refunderList.size + 1))
        val amountPerMember = splitAmount.toBigDecimal().setScale(2, RoundingMode.CEILING).toFloat()

        val receiver: User = userRepository.findByEmail(username)?: return "UserEmail Does not exist"
        val troop: Troop? = if(splitDTO.troopName == "") null else troopRepository.findByName(splitDTO.troopName)

        val paymentList = mutableListOf<PaymentDTO>()
        val splitId:String = generateSplitId()
        for (userId in refunderList) {
            val refunder: User = userRepository.findByEmail(userId)!!
            if(!troopRepository.existsByNameAndUsers(splitDTO.troopName,refunder)) return "${refunder.email} is not in the troop"
            val payDue = (troop)?.let {
                Payment(
                    splitId = splitId,
                    amount = amountPerMember,
                    refunder = refunder,
                    receiver = receiver,
                    troop = it,
                    status = "unpaid"
                )
            }

            val savedPayment = payDue?.let { paymentRepository.save(it) }
            val saved = savedPayment?.let { PaymentToPaymentDTO(it) }
            if (saved != null) {
                paymentList.add(saved)
            }
        }

        return paymentList.toString()
    }

    override fun paymentsOfUser(userAs: UserAs,username:String): List<PaymentDTO> {
        val userData: User = userRepository.findByEmail(username)!!
        return when (userAs.type) {
            "refunder" -> paymentRepository.findByRefunder(userData).map { PaymentToPaymentDTO(it) }
            "receiver" -> paymentRepository.findByReceiver(userData).map { PaymentToPaymentDTO(it) }
            else -> {
                val paymentAsRefunder = paymentRepository.findByRefunder(userData).map { PaymentToPaymentDTO(it) }
                val paymentAsReceiver = paymentRepository.findByReceiver(userData).map { PaymentToPaymentDTO(it) }
                (paymentAsRefunder + paymentAsReceiver)
            }
        }
    }

    override fun payDue(payDue: PayDue,username:String): PaymentDTO {
        val user:User = userRepository.findByEmail(username) ?: throw NotFoundException()
        val paymentCard: Payment = paymentRepository.findBySplitIdAndRefunder(payDue.splitId,user).get(0)
        if(paymentCard.status=="paid") return PaymentToPaymentDTO(paymentCard)
        if(paymentCard.amount<=payDue.amount){
        paymentCard.status = "paid"
        }
        else{
            paymentCard.amount -= payDue.amount
        }
        val result: Payment = paymentRepository.save(paymentCard)
        return PaymentToPaymentDTO(result)
    }

    override fun expenseDetails(username:String): ExpenseDetails {
        val user = userRepository.findByEmail(username)!!

        var paymentList:List<Payment> = paymentRepository.findByRefunder(user).filter { it.status=="paid" }
        val totalAmountPaid = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = paymentRepository.findByRefunder(user).filter { it.status == "unpaid" }
        val totalAmountDue = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = paymentRepository.findByReceiver(user).filter { it.status == "paid" }
        val totalAmountRecieved = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = paymentRepository.findByReceiver(user).filter { it.status == "unpaid" }
        val totalAmountLeft = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        return ExpenseDetails(
                email=username,
                totalAmountPaid = totalAmountPaid,
                totalAmountDue =  totalAmountDue,
                totalAmountRecieved = totalAmountRecieved,
                totalAmountLeft =  totalAmountLeft
        )

    }

    override fun splitStatus(splitId: String,username:String): SplitStatus {
            val paymentDetails:List<Payment> = paymentRepository.findBySplitId(splitId)
            if(userNotIncludeInSplit(paymentDetails,username,false))
            return SplitStatus(
            splitId = splitId,
            receiver = null,
            PaidList = listOf(),
            DueList = listOf(),
            amount = 0F
            )


        val receiver:UserEmailDTO = UserToUserEmailDTO(paymentDetails.get(0).receiver!!)
        val amount:Float = paymentDetails.get(0).amount

        val PaidList: List<UserEmailDTO> = paymentDetails.filter { it.status=="paid" }.map { UserToUserEmailDTO(it.refunder!!) }
        val DueList: List<UserEmailDTO> = paymentDetails.filter { it.status=="unpaid" }.map { UserToUserEmailDTO(it.refunder!!) }
            return SplitStatus(
                splitId = splitId,
                receiver = receiver,
                PaidList = PaidList,
                DueList = DueList,
                amount = amount
            )
    }

    override fun sendReminder(splitId: String, username:String): String {
        val user = userRepository.findByEmail(username)
        val paymentReminderList: List<Payment> = paymentRepository.findBySplitIdAndReceiver(splitId,user!!).filter { it.status=="unpaid" }

        if(paymentReminderList.isEmpty()) return "No Payment Exist"

        val emailNotFound = mutableListOf<String>()
        for(reminder in paymentReminderList){
        val subject: String = reminder.splitId + " Due Reminder!!"
        val message: String = "SPLITID: "+reminder.splitId + " is been due\nAmount: "+reminder.amount+"\nPay To: "+reminder.receiver?.email
            val emailDetail:EmailDetails = reminder.refunder?.let {
                EmailDetails(
                    recipient = it.email,
                    subject = subject,
                    msgBody = message
                )
            }!!
            if(!emailServiceImpl.sendSimpleMail(emailDetail)) emailNotFound.add(reminder.refunder?.email!!)
        }



        if(emailNotFound.isNotEmpty()) return emailNotFound.joinToString(", ") { " email not found" }

        return "Reminders sent successfully"
    }

    fun userNotIncludeInSplit(paymentList:List<Payment>,username:String,needToDelete:Boolean):Boolean{
        var validList:List<Payment> = paymentList.filter { pay->
            pay.receiver!!.email.contains(username) || pay.refunder!!.email.contains(username)
        }
        if(needToDelete){
            validList= paymentList.filter { pay->
                pay.receiver!!.email.contains(username)
            }
        }

        return validList.isEmpty()
    }


    fun deleteTheSplit(splitId: String,username:String):Any{
        val paymentDetails:List<Payment> = paymentRepository.findBySplitId(splitId)
        if(userNotIncludeInSplit(paymentDetails,username,true))
            return "You are not the receiver of the split"

        var paymentPaidAlready:List<Payment> = paymentDetails.filter { it.status=="paid" }

        for(payment in paymentPaidAlready){
            paymentRepository.deleteById(payment.id)
        }

        var paymentDueByUser = mutableListOf<Payment>()
        for(payment in paymentPaidAlready){
            payment.splitId = generateSplitId()
            val refunderToReciever = payment.refunder
            payment.refunder = payment.receiver
            payment.receiver = refunderToReciever
            payment.status = "unpaid"
            val result = paymentRepository.save(payment)
            paymentDueByUser.add(result)
        }

        return if(paymentDueByUser.isEmpty()) "$splitId has been deleted"
        else paymentDueByUser.map { it ->
            PaymentDTO(
                splitId = it.splitId!!,
                amount = it.amount,
                refunder = UserToUserEmailDTO(it.refunder!!),
                receiver = UserToUserEmailDTO(it.receiver!!),
                troop = TroopToTroopDetailsDTO(it.troop!!),
                status = it.status!!
            )
        }
        }

    private fun generateSplitId():String{
        var splitId:String
        do{
            splitId = "SPLIT"+(0..10000).random()
        }while (paymentRepository.existsBySplitId(splitId))
return splitId
    }
}