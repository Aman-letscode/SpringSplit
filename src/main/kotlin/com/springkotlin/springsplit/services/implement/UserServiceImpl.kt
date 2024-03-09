package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.dto.ExpenseDetails
import com.springkotlin.springsplit.dto.Login
import com.springkotlin.springsplit.dto.UserDTO
import com.springkotlin.springsplit.entities.Payment
import com.springkotlin.springsplit.entities.User
import com.springkotlin.springsplit.repositories.UserRepository
import com.springkotlin.springsplit.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Suppress("UNREACHABLE_CODE")
@Service
class UserServiceImpl(@Autowired var userRepository: UserRepository) :UserService{


    override fun login(credentials: Login): String {


        var result : User? =  userRepository.findByEmail(credentials.email)
        if(result==null)return "User Not found"

        result =  userRepository.findByEmailAndPassword(credentials.email,credentials.password)
        if(result!=null){
            return "Login Successfully"
        }
        else{
            return "User Not found"
        }


    }

    override fun createUser(userDTO: UserDTO):UserDTO {

        val user = User(userDTO.name,userDTO.email,userDTO.password)
        var result:User = userRepository.save(user)
        return UserDTO(result.name,result.email,result.password)
    }
    fun displayAllUser():List<User> = userRepository.findAll()

    override fun expenseDetails(credentials: Login): ExpenseDetails {
        val user:User = userRepository.findByEmail(credentials.email)
        var totalAmountPaid:Float = 0F
        var totalAmountDue:Float = 0F
        var totalAmountRecieve:Float = 0F
        var totalAmountLeft:Float = 0F

        var paymentList: List<Payment> = user.refunderPayments.filter{it.status=="paid"}
        totalAmountPaid = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = user.refunderPayments.filter{it.status=="unpaid"}
        totalAmountDue = paymentList.sumOf { it.amount.toDouble() }.toFloat()


        paymentList = user.receiverPayments.filter{it.status=="paid"}
        totalAmountRecieve = paymentList.sumOf { it.amount.toDouble() }.toFloat()

        paymentList = user.receiverPayments.filter{it.status=="unpaid"}
        totalAmountRecieve = paymentList.sumOf { it.amount.toDouble() }.toFloat()


        val result:ExpenseDetails = ExpenseDetails(credentials.email,totalAmountPaid,totalAmountDue,totalAmountRecieve)
        return result
    }




}