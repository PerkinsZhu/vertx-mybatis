package com.perkins.compponent

import com.alibaba.druid.pool.DruidDataSource
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import javax.sql.DataSource

@Component
class MyBeanFactoryPostProcessor : BeanFactoryPostProcessor {
    val logger = LoggerFactory.getLogger(this.javaClass)
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val factory: DefaultListableBeanFactory = beanFactory as DefaultListableBeanFactory
        //TODO 考虑把所有的配置文件参数校验集中到一个检验类中，系统启动时对配置文件做校验，校验失败则不启动
        //TODO  禁止每个service直接从json文件中去参数，而是通过封装的工具类来取值
        val mybatisConfiguration = beanFactory.getBean("config")
        val config = mybatisConfiguration as JsonObject
        val dataSourceList = config.getJsonObject("mybatis", JsonObject()).getJsonArray("dataSource", JsonArray())
        registerDataSourceDefinitions(dataSourceList, factory)
    }


    private fun registerDataSourceDefinitions(dataSourceList: JsonArray, beanFactory: DefaultListableBeanFactory) {
        dataSourceList.forEachIndexed { index, config ->
            val dataSource = createDataSource(config as JsonObject)
            beanFactory.registerSingleton("dataSource-$index", dataSource)

            val sqlSessionFactoryBean = createSqlSessionFactoryBean(dataSource, config)
            val sqlSessionFactoryBeanName = "sqlSessionFactoryBean-$index"
            beanFactory.registerSingleton(sqlSessionFactoryBeanName, sqlSessionFactoryBean)

            val transactionManager = createTransactionManager(dataSource)
            val transactionManagerName = config.getString("name", "$index")
            beanFactory.registerSingleton("transactionManager-$transactionManagerName", transactionManager)

            updateMapperDataSource(sqlSessionFactoryBean.`object`, config, beanFactory)
        }
    }

    private fun createTransactionManager(dataSource: DataSource): DataSourceTransactionManager {
        val transactionManager = DataSourceTransactionManager()
        //这里可以接受一些用户需要配置的参数
        transactionManager.dataSource = dataSource
        return transactionManager
    }

    private fun updateMapperDataSource(
        sqlSessionFactory: SqlSessionFactory,
        config: JsonObject,
        beanFactory: DefaultListableBeanFactory
    ) {
        val scanPackage = config.getJsonArray("scanPackage", JsonArray())
        when (scanPackage.isEmpty) {
            true -> {
                logger.warn("dataSource not config scanPackage")
            }
            false -> {
                beanFactory.beanDefinitionNames.forEach { name ->
                    run {
                        val definition = beanFactory.getBeanDefinition(name)
                        val source = definition.source
                        if (source is FileSystemResource && definition.beanClassName.contains("MapperFactoryBean")) {
                            val classPath =
                                source.path.substringAfter("classes").replace("\\", ".").replace("/", ".").substring(1)
                            if (scanPackage.any { classPath.startsWith(it.toString()) }) {
                                logger.info("$name set $sqlSessionFactory")
                                definition.propertyValues.add("sqlSessionFactory", sqlSessionFactory)
                            }
                        }
                    }
                }
            }
        }
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
            val resolver = PathMatchingResourcePatternResolver()
            val path = config.getString("mapperLocations", "classpath:*Mapper.xml")
            logger.info("path:$path")
            val mapperXml = resolver.getResources(path)
            sessionFactory.setMapperLocations(mapperXml)
        } catch (e: IOException) {
            logger.error("create SqlSessionFactoryBean error", e)
        }
        return sessionFactory
    }
}
