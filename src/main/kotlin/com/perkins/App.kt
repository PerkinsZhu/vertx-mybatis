package com.perkins

import com.perkins.services.UserService
import com.perkins.service.PersonService
import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.AnnotationConfigApplicationContext

object AppKotlin {
    @JvmStatic
    fun main(args: Array<String>) {
//        testUserService()
        testPersonService()
    }

    private fun testPersonService() {
        val appContext = AnnotationConfigApplicationContext("com.perkins")
        val userService = appContext.getBean(PersonService::class.java)
        userService.show()
    }

    private fun testUserService() {
        val appContext = AnnotationConfigApplicationContext("com.perkins")
        val userService = appContext.getBean(UserService::class.java)
        userService.show()
    }
}