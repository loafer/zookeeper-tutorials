package com.github.loafer.rpc;

import com.github.loafer.rpc.annotation.ServiceProvider;

/**
 * @author zhaojh.
 */
@ServiceProvider(
        name = "HelloService",
        type = ExampleService.class,
        description = "Hello Service"
)
public class ExampleServiceImpl implements ExampleService {
    @Override
    public String sayHello(String name) {
        return String.format("Hello %s", name);
    }
}
