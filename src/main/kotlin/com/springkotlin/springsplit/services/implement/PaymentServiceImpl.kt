package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.config.JWTGenerator
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

    val jwtGenerator:JWTGenerator = JWTGenerator()

    override fun createExpense(splitDTO: SplitDTO,token: String): String {
        if(splitDTO.amount<=0) return "Amount cannot be less than or equal to 0"
        val userEmail = extractEmailFromToken(token)
        val user:User = userRepository.findByEmail(userEmail)!!
        var refunderList: List<String> = splitDTO.splitList.filter { it!=userEmail }
        if(splitDTO.troopName == "") return "Enter User Troop Name"
        if(!troopRepository.existsByNameAndUsers(splitDTO.troopName,user)) return userEmail + " is not part of the Troop: "+ splitDTO.troopName
        if (refunderList.isEmpty()) {
            refunderList = troopRepository.findByName(splitDTO.troopName).users.toList().map { it.email }.filter { it != userEmail }
        }
        val splitAmount = ((splitDTO.amount).toFloat() / (refunderList.size + 1))
        val amountPerMember = splitAmount.toBigDecimal().setScale(2, RoundingMode.CEILING).toFloat()

        val receiver: User = userRepository.findByEmail(userEmail)?: return "UserEmail Does not exist"
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

    override fun paymentsOfUser(userAs: UserAs,token: String): List<PaymentDTO> {
        val userData: User = userRepository.findByEmail(extractEmailFromToken(token))!!
        if(userAs.type == "refunder") return paymentRepository.findByRefunder(userData).map { PaymentToPaymentDTO(it) }
        else if (userAs.type == "receiver") return paymentRepository.findByReceiver(userData).map { PaymentToPaymentDTO(it) }
        else{
            val paymentAsRefunder = paymentRepository.findByRefunder(userData).map { PaymentToPaymentDTO(it) }
            val paymentAsReceiver = paymentRepository.findByReceiver(userData).map { PaymentToPaymentDTO(it) }
            return (paymentAsRefunder + paymentAsReceiver)
        }
    }

    override fun payDue(payDue: PayDue,token:String): PaymentDTO {
        val userEmail = extractEmailFromToken(token)
        val user:User = userRepository.findByEmail(userEmail) ?: throw NotFoundException()
        var paymentCard: Payment = paymentRepository.findBySplitIdAndRefunder(payDue.splitId,user).get(0)
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

    override fun expenseDetails(token: String): ExpenseDetails {
        val email = extractEmailFromToken(token)
        val user = userRepository.findByEmail(email)!!

        var paymentList:List<Payment> = paymentRepository.findByRefunder(user).filter { it.status=="paid" }
        val totalAmountPaid = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = paymentRepository.findByRefunder(user).filter { it.status == "unpaid" }
        val totalAmountDue = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = paymentRepository.findByReceiver(user).filter { it.status == "paid" }
        val totalAmountRecieved = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = paymentRepository.findByReceiver(user).filter { it.status == "unpaid" }
        val totalAmountLeft = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        return ExpenseDetails(
                email=email,
                totalAmountPaid = totalAmountPaid,
                totalAmountDue =  totalAmountDue,
                totalAmountRecieved = totalAmountRecieved,
                totalAmountLeft =  totalAmountLeft
        )

    }

    override fun splitStatus(splitId: String,token:String): SplitStatus {

            var username = extractEmailFromToken(token)

            val paymentDetails:List<Payment> = paymentRepository.findBySplitId(splitId)
            if(userNotIncludeInSplit(paymentDetails,username,false))
            return SplitStatus(
            splitId = splitId,
            receiver = null,
            PaidList = listOf(),
            DueList = listOf(),
            amount = 0F
            )


        var receiver:UserEmailDTO = UserToUserEmailDTO(paymentDetails.get(0).receiver!!)
        var amount:Float = paymentDetails.get(0).amount

        var PaidList:List<UserEmailDTO> = paymentDetails.filter { it.status=="paid" }.map { UserToUserEmailDTO(it.refunder!!) }
        var DueList:List<UserEmailDTO> = paymentDetails.filter { it.status=="unpaid" }.map { UserToUserEmailDTO(it.refunder!!) }
            return SplitStatus(
                splitId = splitId,
                receiver = receiver,
                PaidList = PaidList,
                DueList = DueList,
                amount = amount
            )
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


    fun deleteTheSplit(splitId: String,token: String):Any{
        var username = extractEmailFromToken(token)

        val paymentDetails:List<Payment> = paymentRepository.findBySplitId(splitId)
        if(userNotIncludeInSplit(paymentDetails,username,true))
            return "You are not the receiver of the split"

        val paymentPaidAlready:List<Payment> = paymentDetails.filter { it.status=="paid" }

        for(payment in paymentPaidAlready){
            payment.splitId = generateSplitId()
            val refunderToReciever = payment.refunder
            payment.refunder = payment.receiver
            payment.receiver = refunderToReciever
            payment.status = "unpaid"
            val result = paymentRepository.save(payment)
        }

        val paymentDue:List<Payment> = paymentDetails.filter { it.status=="unpaid" }
        for(payment in paymentPaidAlready){
            paymentRepository.deleteById(payment.id)
        }

        if(paymentPaidAlready.isEmpty()) return splitId + " has been deleted"
        else return paymentPaidAlready.map { it ->
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

    private fun extractEmailFromToken(token:String):String = jwtGenerator.generateUserNameByJWT(token.substring(7, token.length))

    private fun generateSplitId():String{
        var splitId:String
        do{
            splitId = "SPLIT"+(0..10000).random()
        }while (paymentRepository.existsBySplitId(splitId))
return splitId
    }
}