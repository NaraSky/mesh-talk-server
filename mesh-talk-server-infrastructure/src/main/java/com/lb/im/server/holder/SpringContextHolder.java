package com.lb.im.server.holder;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * SpringContextHolder 类用于持有 Spring 的 ApplicationContext 实例，
 * 通过实现 ApplicationContextAware 接口注入上下文，并提供静态方法获取 Bean 或上下文对象。
 * 所有获取方法在调用前会检查 ApplicationContext 是否为 null，确保上下文已正确初始化。
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        checkNull();
        return applicationContext;
    }

    /**
     * 根据 Bean 名称获取对应的 Bean 实例。
     *
     * @param beanName Spring 容器中注册的 Bean 名称
     * @return 指定名称的 Bean 实例，类型由泛型指定
     * @throws IllegalStateException 若 ApplicationContext 未被正确初始化（值为 null）
     * @throws BeansException        若 Bean 获取过程中发生异常
     */
    public static <T> T getBean(String beanName) {
        checkNull();
        return (T) applicationContext.getBean(beanName);
    }

    /**
     * 根据类型获取对应的 Bean 实例。
     *
     * @param requiredType 需要获取的 Bean 的 Class 类型
     * @return 指定类型的 Bean 实例
     * @throws IllegalStateException 若 ApplicationContext 未被正确初始化（值为 null）
     * @throws BeansException        若 Bean 获取过程中发生异常
     */
    public static <T> T getBean(Class<T> requiredType) {
        checkNull();
        return applicationContext.getBean(requiredType);
    }

    /**
     * 检查 ApplicationContext 是否为 null，若为 null 则抛出异常。
     *
     * @throws IllegalStateException 若 ApplicationContext 未被正确初始化（值为 null）
     */
    private static void checkNull() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicationContext is null");
        }
    }
}
