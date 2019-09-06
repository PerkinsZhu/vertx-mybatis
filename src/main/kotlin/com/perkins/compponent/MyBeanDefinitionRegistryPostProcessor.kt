package com.perkins.compponent

import com.perkins.mapper3.UserMapper3
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.stereotype.Component

@Component
class MyBeanDefinitionRegistryPostProcessor : BeanDefinitionRegistryPostProcessor {
    override fun postProcessBeanDefinitionRegistry(registtry: BeanDefinitionRegistry) {
//       val className = UserMapper3::class.java
      /*  val def = BeanDefinitionBuilder.genericBeanDefinition(className).beanDefinition
        registtry.registerBeanDefinition("userMapper3",def)*/

        val mapper3 = registtry.getBeanDefinition("userMapper3")
    /*    mapper3.setAttribute("sqlSessionTemplate",sqlSessionTemplateBeanName)
        beanFactory.removeBeanDefinition("userMapper3")
        beanFactory.registerBeanDefinition("userMapper3",mapper3)*/

    }

    override fun postProcessBeanFactory(registtry: ConfigurableListableBeanFactory) {
        val s = registtry.beanNamesIterator
    }
}