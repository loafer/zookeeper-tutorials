package com.github.loafer.rpc;

import com.github.loafer.rpc.annotation.ServiceProvider;
import com.github.loafer.rpc.annotation.ServiceProviders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaojh.
 */
public class ServiceDiscovery implements InitializingBean, BeanPostProcessor, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private Map<String, ServiceInstance> providerMap = new HashMap<String, ServiceInstance>();
    private ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        bootstrap();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if(bean.getClass().isAnnotationPresent(ServiceProviders.class)){
            ServiceProviders serviceProviders = bean.getClass().getAnnotation(ServiceProviders.class);
            ServiceProvider[] providers = serviceProviders.value();
            for (ServiceProvider provider : providers){
                ServiceInstance serviceInstance = buildServiceInstance(provider, bean);
                registerService(serviceInstance);
            }
        }

        if(bean.getClass().isAnnotationPresent(ServiceProvider.class)){
            ServiceProvider provider = bean.getClass().getAnnotation(ServiceProvider.class);
            ServiceInstance serviceInstance = buildServiceInstance(provider, bean);
            registerService(serviceInstance);
        }

        return bean;
    }

    @Override
    public void destroy() throws Exception {
    }

    private void bootstrap() throws Exception {

    }

    private void registerService(ServiceInstance serviceInstance) throws ServiceRegisterException{
        if(providerMap.containsKey(serviceInstance.getName())){
            return;
        }

        logger.info("register [{}]", serviceInstance.getName());
        providerMap.put(serviceInstance.getName(), serviceInstance);
        try {
            serviceRegistry.registerService(serviceInstance);
        } catch (Exception e) {
            String message = String.format("[%s] Registration failed.", serviceInstance.getName());
            throw new ServiceRegisterException(message, e);
        }
    }

    private ServiceInstance buildServiceInstance(ServiceProvider provider, Object bean){
        ServiceInstance instance = new ServiceInstance(provider.name(), bean);
        instance.setDescription(provider.description());
        instance.setClassType(provider.type());
        return instance;
    }
}
