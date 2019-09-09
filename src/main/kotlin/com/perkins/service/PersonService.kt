package com.perkins.service

import com.perkins.entity.User
import com.perkins.mapper0.UserMapper0
import com.perkins.mapper1.UserMapper1
import com.perkins.mapper2.User2Mapper
import com.perkins.mapper3.User3Mapper
import org.mybatis.spring.annotation.MapperScan
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.Transactional
import vts.vertxbeans.rxjava.VertxBeans


@Service("personService")
@Import(VertxBeans::class)
@MapperScan(basePackages = ["com.perkins.mapper0", "com.perkins.mapper1", "com.perkins.mapper2", "com.perkins.mapper3"])
@EnableTransactionManagement
open class PersonService {
    val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var mapper0: UserMapper0
    @Autowired
    lateinit var mapper1: UserMapper1
    @Autowired
    lateinit var user2Mapper: User2Mapper
    @Autowired
    lateinit var user3Mapper: User3Mapper

    open fun show() {
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

    // 夸数据源事务时，该配置不生效
    // value 指定该事务使用哪个    transactionManager，默认格式：transactionManager-${dataSourceName}
    @Transactional(value = "transactionManager-db1")
    open fun transactionalTest() {
        val user = User()
        user.name = "aaa"
        user.age = 20
        val id = mapper0.save(user)
        println(id)
        println(user.id)

        val id2 = mapper1.save(user)
        println(id2)
        println(user.id)

//        1 / 0
        mapper0.listUser()
    }


}