package com.omv.common.util.basic;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class BeanUtil implements ApplicationContextAware {

    // Spring Bean 工厂

    public static DefaultListableBeanFactory beanFactory;

    //注意在spring初始化刷新后要调用这个init方法来获得新的beanFactory对象
    public static void init(ApplicationContext applicationContext) {
        beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }

    /**
     * 动态增加bean
     *
     * @param className
     * @param beanKey
     * @param propertyMap
     * @param referenceMap
     * @return
     */
    public static boolean addBean(
            String className, String beanKey, Map<String, Object> propertyMap,
            Map<String, String> referenceMap, List<Object> propertyConstrList,
            List<String> referenceConstrList, String destroyMethod) {
        // 判断Spring容器中是否存在该Bean
        if (beanFactory.containsBeanDefinition(className)) {
            return true;
        }

        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(className);
        bdb.getBeanDefinition().setAttribute("id", beanKey);

        // 设置bean参数属性
        if (propertyMap != null && !propertyMap.isEmpty()) {
            Iterator<String> propertyKeys = propertyMap.keySet().iterator();
            while (propertyKeys.hasNext()) {
                String keyString = propertyKeys.next();
                bdb.addPropertyValue(keyString, propertyMap.get(keyString));
            }
        }

        // 设置bean参数引用
        if (referenceMap != null && !referenceMap.isEmpty()) {
            Iterator<String> referenceKeys = referenceMap.keySet().iterator();
            while (referenceKeys.hasNext()) {
                String keyString = referenceKeys.next();
                bdb.addPropertyReference(keyString, referenceMap.get(keyString));
            }
        }

        // 设置bean构造参数属性
        if (propertyConstrList != null && !propertyConstrList.isEmpty()) {
            for (int i = 0; i < propertyConstrList.size(); i++) {
                bdb.addConstructorArgValue(propertyConstrList.get(i));
            }
        }

        // 设置bean构造参数引用
        if (referenceConstrList != null && !referenceConstrList.isEmpty()) {
            for (int i = 0; i < referenceConstrList.size(); i++) {
                bdb.addConstructorArgReference(referenceConstrList.get(i));
            }
        }

        if (destroyMethod != null && !destroyMethod.isEmpty()) {
            bdb.setDestroyMethodName(destroyMethod);
        }

        beanFactory.registerBeanDefinition(beanKey, bdb.getBeanDefinition());

        return true;
    }

    /**
     * 获取Bean
     *
     * @param requiredType
     * @return
     */
    public static <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    /**
     * 获取bean
     *
     * @param beanName
     * @param requiredType
     * @return
     */
    public static <T> T getBean(String beanName, Class<T> requiredType) {
        return beanFactory.getBean(beanName, requiredType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        init(applicationContext);
    }
}