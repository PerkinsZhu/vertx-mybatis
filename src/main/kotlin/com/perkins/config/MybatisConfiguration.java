package com.perkins.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.perkins.datasources.DynamicDataSource;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configurable
//@MapperScan(basePackages = {"com.perkins.mapper3", "com.perkins.mapper4"},sqlSessionFactoryRef = "basicSqlSessionFactory")
public class MybatisConfiguration {

    private static Logger logger = LoggerFactory.getLogger(vts.vertxmybatis.MybatisConfiguration.class);
    public static String CONFIG_FILE = "config.json";

    @Autowired
    public JsonObject config;


    @Bean("mybatisConfiguration")
    public JsonObject localConfig() {
        JsonObject localConfig = new JsonObject();
        try {
            Resource res = new ClassPathResource(CONFIG_FILE);
            StringBuilder stringBuffer = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null) {
                stringBuffer.append(str);
            }
            in.close();
            localConfig = new JsonObject(stringBuffer.toString());
        } catch (Exception e) {
            logger.error(e);
        }
        localConfig = config.mergeIn(localConfig, true);
        logger.info(localConfig);
        return localConfig;
    }


    @Bean("db2DataSource")
    public DataSource db2DataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        Properties properties = new Properties();
        JsonObject conf = localConfig().getJsonObject("dataSource", new JsonObject());
        conf.iterator().forEachRemaining(it -> properties.put("druid." + it.getKey(), it.getValue()));
        dataSource.configFromPropety(properties);
        return dataSource;
    }

    @Bean("db1DataSource")
    public DataSource db1DataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        Properties properties = new Properties();
        JsonObject conf = localConfig().getJsonObject("db1.dataSource", new JsonObject());
        conf.iterator().forEachRemaining(it -> properties.put("druid." + it.getKey(), it.getValue()));
        dataSource.configFromPropety(properties);
        return dataSource;
    }

    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put("db1", db2DataSource());
        dataSourceMap.put("db2", db1DataSource());
        // 将 master 数据源作为默认指定的数据源
        dynamicDataSource.setDefaultDataSource(db2DataSource());
        // 将 master 和 slave 数据源作为指定的数据源
        dynamicDataSource.setDataSources(dataSourceMap);
        return dynamicDataSource;
    }

    @Primary
    @Bean(name = "basicSqlSessionFactory")
    public SqlSessionFactoryBean basicSqlSessionFactoryBean() throws IOException {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dynamicDataSource());
        try {
            // 设置mybatis configuration 扫描路径
            JsonObject conf = localConfig().getJsonObject("mybatis", new JsonObject());
            ClassPathResource classPathResource = new ClassPathResource(conf.getString("configLocation", "mybatis-config.xml"));
            if (classPathResource.exists()) {
                sessionFactory.setConfigLocation(classPathResource);
            }
            // 设置typeAlias 包扫描路径
            if (conf.containsKey("typeAliasesPackage")) {
                sessionFactory.setTypeAliasesPackage(conf.getString("typeAliasesPackage"));
            }
            // 添加mapper 扫描路径
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] mapperXml = resolver.getResources(conf.getString("mapperLocations", "classpath:*Mapper.xml"));
            sessionFactory.setMapperLocations(mapperXml);
        } catch (IOException e) {
            logger.error(e);
        }
        return sessionFactory;
    }
/*    @Bean("basicMapperScannerConfigurer")
    public MapperScannerConfigurer basicMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        JsonObject conf = localConfig().getJsonObject("mybatis", new JsonObject());
        mapperScannerConfigurer.setBasePackage(conf.getString("basePackage"));
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(conf.getString("basicSqlSessionFactory","basicSqlSessionFactory"));
        return mapperScannerConfigurer;
    }*/

}
