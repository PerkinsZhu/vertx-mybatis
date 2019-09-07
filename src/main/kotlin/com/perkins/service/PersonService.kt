package com.perkins.service

import com.perkins.mapper0.UserMapper0
import com.perkins.mapper1.UserMapper1
import com.perkins.mapper2.User2Mapper
import com.perkins.mapper3.User3Mapper
import org.mybatis.spring.annotation.MapperScan
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Service
import vts.vertxbeans.rxjava.VertxBeans


@Service("personService")
@Import(VertxBeans::class)
@MapperScan(basePackages = ["com.perkins.mapper0", "com.perkins.mapper1", "com.perkins.mapper2", "com.perkins.mapper3"])
class PersonService {
    val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var mapper0: UserMapper0
    @Autowired
    lateinit var mapper1: UserMapper1
    @Autowired
    lateinit var user2Mapper: User2Mapper
    @Autowired
    lateinit var user3Mapper: User3Mapper

    fun show() {
        println("=====mapper0====")
        mapper0.listUser().forEach { user ->
            println(user)
        }
        println("=====mapper1====")
        mapper1.listUser().forEach { user ->
            println(user)
        }
        println("=====user2Mapper====")
        user2Mapper.listUser().forEach { user ->
            println(user)
        }
        println("=====user3Mapper====")
        user3Mapper.listUser().forEach { user ->
            println(user)
        }
    }

}