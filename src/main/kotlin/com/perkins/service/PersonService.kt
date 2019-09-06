package com.perkins.service

import com.perkins.config.MybatisConfiguration
import com.perkins.datasources.DynamicDataSource
import com.perkins.datasources.DynamicDataSourceContextHolder
import com.perkins.mapper3.UserMapper3
import com.perkins.mapper4.UserMapper4
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Service
import vts.vertxbeans.rxjava.VertxBeans
import java.sql.Connection
import javax.annotation.Resource


@Service("personService")
@Import(VertxBeans::class, MybatisConfiguration::class)
class PersonService {

/*
    @Autowired
    lateinit var mapper3: UserMapper3

    @Autowired
    lateinit var mapper4: UserMapper4

*/

    @Resource(name = "dynamicDataSource")
    lateinit var dynamicDataSource: DynamicDataSource

    fun show() {

        DynamicDataSourceContextHolder.setDataSourceKey("db1")
        doQuery()
        println("===========")
        DynamicDataSourceContextHolder.setDataSourceKey("db2")
        doQuery()

/*
        println(dynamicDataSource)
        mapper3.listUser().forEach { user ->
            println(user)
        }
//        DynamicDataSourceContextHolder.dataSourceKey = "db2"
        println("=============")
        mapper4.listUser().forEach { user ->
            println(user)
        }
*/

        println(dynamicDataSource)
    }

    private fun doQuery() {
        val sql = "SELECT * FROM user"
        val preparedStatement = dynamicDataSource.connection.prepareStatement(sql)

        val resultSet = preparedStatement.executeQuery()
        while (resultSet.next()) {
            println(resultSet.getInt(1))
        }
    }

}