package com.perkins.service

import com.perkins.mapper3.UserMapper3
import com.perkins.mapper4.UserMapper4
import org.mybatis.spring.annotation.MapperScan
import org.mybatis.spring.mapper.MapperScannerConfigurer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Service
import vts.vertxbeans.rxjava.VertxBeans
import javax.annotation.Resource
import javax.sql.DataSource


@Service("personService")
@Import(VertxBeans::class)
@MapperScan(basePackages = ["com.perkins.mapper3","com.perkins.mapper4"])
class PersonService {

/*    @Resource(name = "dataSource-0")
    lateinit var dataSource0: DataSource

    @Resource(name = "dataSource-1")
    lateinit var dataSource1: DataSource

    */

    @Resource(name = "mapperScannerConfigurer-1")
    lateinit var mapperScanner: MapperScannerConfigurer


    @Autowired
    lateinit var mapper3: UserMapper3

    @Autowired
    lateinit var mapper4: UserMapper4



    fun show() {
//        println(dataSource0)
//        println(dataSource1)
        println(mapperScanner)

        println("PersonService")
        mapper3.listUser().forEach { user ->
            println(user)
        }
        println("=============")
        mapper4.listUser().forEach { user ->
            println(user)
        }
    }

}