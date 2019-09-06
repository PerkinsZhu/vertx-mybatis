package com.perkins.compponent

import com.perkins.servvice.BookService
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.stereotype.Component

@Component
class MyBeanFactoryPostProcessor : BeanFactoryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val factory: DefaultListableBeanFactory = beanFactory as DefaultListableBeanFactory
        //Bean 定义
        val builder: BeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(BookService().javaClass)
        builder.addPropertyReference("userService", "userService")
        builder.addPropertyValue("name", "serviceName")
        //注册 Bean 定义
        factory.registerBeanDefinition("bookService1", builder.rawBeanDefinition)
        //注册 Bean 实例
        factory.registerSingleton("bookService2", BookService());
    }

}
