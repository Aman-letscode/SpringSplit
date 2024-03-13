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

    override fun createExpense(splitDTO: SplitDTO,token: String): Any {

        val user:User = userRepository.findByEmail(extractEmailFromToken(token))!!
        var refunderList: List<String> = splitDTO.splitList
        println(troopRepository.existsByNameAndUsers(splitDTO.troopName,user))
        if(splitDTO.troopName.isEmpty() || !troopRepository.existsByNameAndUsers(splitDTO.troopName,user)) return "User Not Part of the tru"
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
        var splitId:String = generateSplitId()

        for (userId in refunderList) {
            val refunder: User = userRepository.findByEmail(userId)!!
            val payDue = Payment(
                splitId = splitId,
                amount = amountPerMember,
                refunder = refunder,
                receiver = receiver,
                troop = troop,
                status = "unpaid"
            )

            val savedPayment = paymentRepository.save(payDue)
            val saved = PaymentDTO(
                savedPayment.splitId!!,
                savedPayment.amount,
                refunder = UserToUserEmailDTO(savedPayment.refunder!!),
                receiver = UserToUserEmailDTO(savedPayment.receiver!!),
                troop = TroopToTroopDetailsDTO(savedPayment.troop!!),
                status = savedPayment.status!!
            )
            paymentList.add(saved)
        }

        return paymentList
    }

    override fun paymentsOfUser(token: String): Any {
        val userData: User = userRepository.findByEmail(extractEmailFromToken(token)) ?: return "User Not Found"
        val paymentAsRefunder: List<Payment> = paymentRepository.findByRefunder(userData)
        val paymentAsReceiver: List<Payment> = paymentRepository.findByReceiver(userData)
            return (paymentAsRefunder + paymentAsReceiver).map { PaymentToPaymentDTO(it) }
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
                email,
                totalAmountPaid,
                totalAmountDue,
                totalAmountRecieve,
                totalAmountLeft
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