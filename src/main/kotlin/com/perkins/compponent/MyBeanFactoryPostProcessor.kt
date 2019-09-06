package com.perkins.compponent

import com.alibaba.druid.pool.DruidDataSource
import com.perkins.service.BookService
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.SqlSessionTemplate
import org.mybatis.spring.mapper.MapperScannerConfigurer
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import javax.sql.DataSource

@Component
class MyBeanFactoryPostProcessor : BeanFactoryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        debug(beanFactory)

        val factory: DefaultListableBeanFactory = beanFactory as DefaultListableBeanFactory

        val mybatisConfiguration = beanFactory.getBean("mybatisConfiguration")
        val config = mybatisConfiguration as JsonObject
        val dataSourceList = config.getJsonObject("mybatis", JsonObject()).getJsonArray("dataSource", JsonArray())
        registerDataSourceDefinitions(dataSourceList, factory)

        debug(beanFactory)
        //Bean 定义
        val builder: BeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(BookService().javaClass)
        builder.addPropertyReference("userService", "userService")
        builder.addPropertyValue("name", "serviceName")
        //注册 Bean 定义
        factory.registerBeanDefinition("bookService1", builder.rawBeanDefinition)
        //注册 Bean 实例
        factory.registerSingleton("bookService2", BookService());
    }

    private fun debug(beanFactory: ConfigurableListableBeanFactory) {
        val mapper3 = beanFactory.getBeanDefinition("userMapper3")
        val iterator = beanFactory.beanDefinitionNames
        val iterator2 = beanFactory.beanNamesIterator
    }

    private fun registerDataSourceDefinitions(dataSourceList: JsonArray, beanFactory: DefaultListableBeanFactory) {
        dataSourceList.forEachIndexed { index, config ->
            val dataSource = createDataSource(config as JsonObject)
            beanFactory.registerSingleton("dataSource-$index", dataSource)

            val sqlSessionFactoryBean = createSqlSessionFactoryBean(dataSource, config)
            val sqlSessionFactoryBeanName = "sqlSessionFactoryBean-$index"
            beanFactory.registerSingleton(sqlSessionFactoryBeanName, sqlSessionFactoryBean)

            val sqlSessionTemplateBeanName = "sqlSessionTemplateBean-$index"
            val sqlSessionTemplate = SqlSessionTemplate(sqlSessionFactoryBean.`object`)
            beanFactory.registerSingleton(sqlSessionTemplateBeanName, sqlSessionTemplate)

            val mapperScannerConfigurer: MapperScannerConfigurer =
                createMapperScannerConfigurer(config, sqlSessionFactoryBeanName, sqlSessionTemplateBeanName)

            beanFactory.registerSingleton("mapperScannerConfigurer-$index", mapperScannerConfigurer)
/*

            val mapper3 = beanFactory.getBeanDefinition("userMapper3")
            mapper3.setAttribute("sqlSessionTemplate",sqlSessionTemplateBeanName)
            beanFactory.removeBeanDefinition("userMapper3")
            beanFactory.registerBeanDefinition("userMapper3",mapper3)
*/

            println("-----")
        }
    }

    private fun createMapperScannerConfigurer(
        config: JsonObject,
        sqlSessionFactoryBeanName: String,
        sqlSessionTemplateBeanName: String
    ): MapperScannerConfigurer {
        val mapperScannerConfigurer = MapperScannerConfigurer()
        val scanPackage = config.getString("basePackage")
        mapperScannerConfigurer.setBasePackage(scanPackage)
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(sqlSessionFactoryBeanName)
        mapperScannerConfigurer.setSqlSessionTemplateBeanName(sqlSessionTemplateBeanName)
//        mapperScannerConfigurer.postProcessBeanDefinitionRegistry()
        return mapperScannerConfigurer
    }

    private fun createDataSource(config: JsonObject): DataSource {
        val dataSource = DruidDataSource()
        val properties = Properties()
        config.iterator().forEachRemaining { properties["druid." + it.key] = it.value }
        dataSource.configFromPropety(properties)
        return dataSource
    }

    private fun createSqlSessionFactoryBean(dataSource: DataSource, config: JsonObject): SqlSessionFactoryBean {
        val sessionFactory = SqlSessionFactoryBean()
        sessionFactory.setDataSource(dataSource)
        try {
            // 添加mapper 扫描路径
            val resolver = PathMatchingResourcePatternResolver()
            val mapperXml = resolver.getResources(config.getString("mapperLocations", "classpath:*Mapper.xml"))
            sessionFactory.setMapperLocations(mapperXml)
        } catch (e: IOException) {
            e.printStackTrace()
            //TODO 日志输出
        }
        return sessionFactory
    }
}
