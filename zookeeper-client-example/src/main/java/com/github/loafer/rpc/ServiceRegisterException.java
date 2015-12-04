package com.github.loafer.rpc;

import org.springframework.beans.BeansException;

/**
 * @author zhaojh.
 */
public class ServiceRegisterException extends BeansException {

    public ServiceRegisterException(String msg) {
        super(msg);
    }

    public ServiceRegisterException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
