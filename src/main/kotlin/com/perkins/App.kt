package com.perkins

import com.perkins.services.UserService
import org.springframework.context.annotation.AnnotationConfigApplicationContext

object AppKotlin {
    @JvmStatic
    fun main(args: Array<String>) {
        val appContext = AnnotationConfigApplicationContext("com.perkins")
        val userService = appContext.getBean(UserService::class.java)
        userService.show()
    }
}