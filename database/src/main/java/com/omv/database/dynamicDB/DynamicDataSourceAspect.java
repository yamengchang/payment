package com.omv.database.dynamicDB;

import com.omv.common.util.basic.LanguageUtil;
import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.userutil.UserUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by zwj on 2018/3/26.
 */
@Aspect
@Order(-10)
@Component
public class DynamicDataSourceAspect {

    //Controller层切点 包路径
    @Pointcut("execution (* com..service..*(..)) || execution (* com.database..*(..))")
    public void datasrouceAspect() {
    }

    /*
   * @Before("@annotation(ds)")
   *的意思是：
   *
   * @Before：在方法执行之前进行执行：
   * @annotation(targetDataSource)：
   *会拦截注解targetDataSource的方法，否则不拦截;
   */
    @Before("datasrouceAspect()")
    public void changeDataSource(JoinPoint joinPoint) throws Throwable {
        //获取当前的指定的数据源;
        String value = "";
        //得到被代理的方法
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException();
        }

        //获取方法的参数名和参数值
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = joinPoint.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        //得到被代理的方法上的注解
        Object annotation = method.getAnnotation(TargetDataSource.class);
        if (annotation != null) {
            value = method.getAnnotation(TargetDataSource.class).value();

            String dsId = value;
            if (StringUtils.isEmpty(dsId)) {
                dsId = LanguageUtil.getLanguage();
            }
            //如果不在我们注入的所有的数据源范围之内，那么输出警告信息，系统自动使用默认的数据源。
            if (DynamicDataSourceContextHolder.containsDataSource(dsId)) {
                System.out.println("UseDataSource : " + dsId);
                //找到的话，那么设置到动态数据源上下文中。
                DynamicDataSourceContextHolder.setDataSourceType(dsId);
            } else {
                System.out.println("UseDataSource : zh_CN");
            }
        }



    }


    @After("datasrouceAspect()")
    public void restoreDataSource(JoinPoint point) {
        //方法执行完毕之后，销毁当前数据源信息，进行垃圾回收。
        DynamicDataSourceContextHolder.clearDataSourceType();

    }
}
