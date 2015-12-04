package com.github.loafer.rpc;

/**
 * @author zhaojh.
 */
public interface ServiceRegistry {
    void registerService(ServiceInstance serviceInstance) throws Exception;
}
