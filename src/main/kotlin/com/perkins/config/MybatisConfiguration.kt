package com.perkins.config

import com.alibaba.druid.pool.DruidDataSource
import io.vertx.core.json.JsonObject
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.SqlSessionTemplate
import org.mybatis.spring.annotation.MapperScan
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.IOException
import java.util.*
import javax.sql.DataSource


@Configurable
@MapperScan(basePackages = ["com.perkins.mapperdb1"], sqlSessionTemplateRef = "db1SqlSessionTemplate")
class SpecialDataSourceConfig {
    private val prefix: String = "db2"
    @Bean(name = ["db1DataSource"])
    fun basicDataSource(@Qualifier("mybatisConfiguration") config: JsonObject): DataSource {
        val dataSource = DruidDataSource()
        val properties = Properties()
        val conf = config.getJsonObject("$prefix", JsonObject()).getJsonObject("dataSource", JsonObject())
        conf.iterator().forEachRemaining { properties["druid." + it.key] = it.value }
        dataSource.configFromPropety(properties)
        return dataSource
    }

    @Bean(name = ["db1SqlSessionFactory"])
    @Throws(IOException::class)
    fun specialSqlSessionFactoryBean(@Qualifier("db1DataSource") dataSource: DataSource, @Qualifier("mybatisConfiguration") config: JsonObject): SqlSessionFactoryBean {
        val sessionFactory = SqlSessionFactoryBean()
        sessionFactory.setDataSource(dataSource)
        try {
            val conf = config.getJsonObject("$prefix", JsonObject()).getJsonObject("mybatis", JsonObject())
            val resolver = PathMatchingResourcePatternResolver()
            val mapperXml = resolver.getResources(conf.getString("mapperLocations", "classpath:*Mapper.xml"))
            sessionFactory.setMapperLocations(mapperXml)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sessionFactory
    }

    @Bean(name = ["db1SqlSessionTemplate"])
    @Throws(Exception::class)
    fun setSqlSessionTemplate(@Qualifier("db1SqlSessionFactory") sqlSessionFactory: SqlSessionFactory): SqlSessionTemplate {
        return SqlSessionTemplate(sqlSessionFactory)
    }
}