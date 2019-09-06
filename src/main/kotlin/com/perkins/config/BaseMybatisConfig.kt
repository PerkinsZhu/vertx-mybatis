package com.perkins.config

import com.alibaba.druid.pool.DruidDataSource
import io.vertx.core.json.JsonObject
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.mapper.MapperScannerConfigurer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.IOException
import java.util.*
import javax.sql.DataSource
import vts.vertxmybatis.MybatisConfiguration as baseMybatisConfig

class BaseMybatisConfig : baseMybatisConfig() {
    val logger = LoggerFactory.getLogger(this.javaClass)
    val prefix = "db1"

    @Bean(name = ["basicDataSource"])
    override fun basicDataSource(@Qualifier("mybatisConfiguration") config: JsonObject): DataSource {
        val dataSource = DruidDataSource()
        val properties = Properties()
        val conf = config.getJsonObject("$prefix", JsonObject()).getJsonObject("dataSource", JsonObject())
        conf.iterator().forEachRemaining { properties["druid." + it.key] = it.value }
        dataSource.configFromPropety(properties)
        return dataSource
    }

    @Primary
    @Bean(name = ["basicSqlSessionFactory"])
    @Throws(IOException::class)
    override fun basicSqlSessionFactoryBean(@Qualifier("basicDataSource") dataSource: DataSource, @Qualifier("mybatisConfiguration") config: JsonObject): SqlSessionFactoryBean {
        val sessionFactory = SqlSessionFactoryBean()
        sessionFactory.setDataSource(dataSource)
        try {
            // 设置mybatis configuration 扫描路径
            val conf = config.getJsonObject("$prefix", JsonObject()).getJsonObject("mybatis", JsonObject())
            val classPathResource = ClassPathResource(conf.getString("configLocation", "mybatis-config.xml"))
            if (classPathResource.exists()) {
                sessionFactory.setConfigLocation(classPathResource)
            }
            // 设置typeAlias 包扫描路径
            if (conf.containsKey("typeAliasesPackage")) {
                sessionFactory.setTypeAliasesPackage(conf.getString("typeAliasesPackage"))
            }
            // 添加mapper 扫描路径
            val resolver = PathMatchingResourcePatternResolver()
            val mapperXml = resolver.getResources(conf.getString("mapperLocations", "classpath:*Mapper.xml"))
            sessionFactory.setMapperLocations(mapperXml)
        } catch (e: IOException) {
            logger.error("====", e)
        }

        return sessionFactory
    }


    @Bean
    override fun basicMapperScannerConfigurer(@Qualifier("mybatisConfiguration") config: JsonObject): MapperScannerConfigurer {
        val mapperScannerConfigurer = MapperScannerConfigurer()
        val conf = config.getJsonObject("$prefix", JsonObject()).getJsonObject("mybatis", JsonObject())
        // Dao package path
        mapperScannerConfigurer.setBasePackage(conf.getString("basePackage"))
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(
            conf.getString(
                "sqlSessionFactoryBeanName",
                "basicSqlSessionFactory"
            )
        )
        return mapperScannerConfigurer
    }
}