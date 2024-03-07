package com.springkotlin.springsplit.services.implement

import com.springkotlin.springsplit.dto.Login
import com.springkotlin.springsplit.dto.UserInfo
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

    override fun createUser(userInfo: UserInfo):UserInfo {

        val user = User(userInfo.name,userInfo.email,userInfo.password)
        var result:User = userRepository.save(user)
        return UserInfo(result.name,result.email,result.password)
    }

}