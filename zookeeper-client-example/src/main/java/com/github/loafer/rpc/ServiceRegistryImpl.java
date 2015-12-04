package com.github.loafer.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * /serviceRegistry
 *     |-{serviceName}
 *           |-providers
 *           |      |-provider-01
 *           |      |-provider-02
 *           |
 *           |-consumers
 *                  |-consumer-01
 *                  |-consumer-02
 *
 * @author zhaojh.
 */
public class ServiceRegistryImpl implements ServiceRegistry, InitializingBean, DisposableBean{
    private static Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);
    private static final String SERVICE_REGISTRY_BASE_PATH = "/serviceRegistry";
    private static final String SERVICE_PATH = SERVICE_REGISTRY_BASE_PATH + "/%s";
    private static final String SERVICE_PROVIDERS_PATH = SERVICE_PATH +"/providers";
    private static final String SERVICE_CONSUMERS_PATH = SERVICE_PATH +"/consumers";
    private static final String SERVICE_PROVIDER_PREFIX = SERVICE_PROVIDERS_PATH + "/provider-";

    private String registryAddress;
    private CuratorFramework curatorFramework;
    private ObjectMapper mapper = new ObjectMapper();

    public ServiceRegistryImpl(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    @Override
    public void destroy() throws Exception {
        disconnectZK();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        bootstrap();
    }

    @Override
    public void registerService(ServiceInstance serviceInstance) throws Exception {
        checkServiceZnode(serviceInstance);
    }

    private void connectZK() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.newClient(registryAddress, retryPolicy);
        curatorFramework.start();
    }

    private void disconnectZK(){
        logger.info("disconnect");
        curatorFramework.close();
    }

    private void bootstrap() throws Exception {
        connectZK();
        checkServiceRegistryZnode();
    }

    private BackgroundCallback checkServiceRegistryCallback = new BackgroundCallback() {
        @Override
        public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent)
                throws Exception {
            switch (Code.get(curatorEvent.getResultCode())) {
                case NONODE:
                    createServiceRegistryZNode();
                    break;
            }
        }
    };

    private void checkServiceRegistryZnode() throws Exception {
        curatorFramework
                .getData()
                .inBackground(checkServiceRegistryCallback)
                .forPath(SERVICE_REGISTRY_BASE_PATH);
    }

    private BackgroundCallback createServiceRegistryCallback = new BackgroundCallback() {
        @Override
        public void processResult(CuratorFramework curatorFramework,
                                  CuratorEvent curatorEvent) throws Exception {
            switch (Code.get(curatorEvent.getResultCode())) {
                case NODEEXISTS:
                    logger.info("===>[{}] already created.", curatorEvent.getPath());
                    break;
            }
        }
    };
    private void createServiceRegistryZNode() throws Exception {
        curatorFramework
                .create()
                .withMode(CreateMode.PERSISTENT)
                .inBackground(createServiceRegistryCallback)
                .forPath(SERVICE_REGISTRY_BASE_PATH, new byte[0]);
    }

    private BackgroundCallback checkServiceCallback = new BackgroundCallback() {
        @Override
        public void processResult(CuratorFramework curatorFramework,
                                  CuratorEvent curatorEvent) throws Exception {

            ServiceInstance serviceInstance = (ServiceInstance) curatorEvent.getContext();
            switch (Code.get(curatorEvent.getResultCode())){
                case NONODE:
                    createServiceZNode(serviceInstance);
                    break;
                case OK:
                    logger.info("===>[{}] already created.", curatorEvent.getPath());
                    createProvidersZNode(serviceInstance);
                    createConsumersZNode(serviceInstance);
                    break;
            }
        }
    };

    private void checkServiceZnode(final ServiceInstance serviceInstance) throws Exception {
        String serviceName = serviceInstance.getName();
        String path = String.format(SERVICE_PATH, serviceName);
        logger.info("===>check [{}].", path);
        curatorFramework
                .getData()
                .inBackground(checkServiceCallback, serviceInstance)
                .forPath(path);
    }

    private BackgroundCallback createServiceCallback = new BackgroundCallback() {
        @Override
        public void processResult(CuratorFramework curatorFramework,
                                  CuratorEvent curatorEvent) throws Exception {
            ServiceInstance serviceInstance = (ServiceInstance) curatorEvent.getContext();
            switch (Code.get(curatorEvent.getResultCode())) {
                case NODEEXISTS:
                    logger.info("===>[{}] already created.", curatorEvent.getPath());
                    break;
                case OK:
                    logger.info("===>[{}] created.", curatorEvent.getPath());
                    createProvidersZNode(serviceInstance);
                    createConsumersZNode(serviceInstance);
                    break;
            }
        }
    };
    private void createServiceZNode(final ServiceInstance serviceInstance) throws Exception {
        final String serviceName = serviceInstance.getName();
        String path = String.format(SERVICE_PATH, serviceName);
        logger.info("===>creating [{}].", path);
        curatorFramework
                .create()
                .withMode(CreateMode.PERSISTENT)
                .inBackground(createServiceCallback, serviceInstance)
                .forPath(path);
    }

    private BackgroundCallback createProvidersCallback = new BackgroundCallback() {
        @Override
        public void processResult(CuratorFramework curatorFramework,
                                  CuratorEvent curatorEvent) throws Exception {
            ServiceInstance serviceInstance = (ServiceInstance) curatorEvent.getContext();
            switch (Code.get(curatorEvent.getResultCode())){
                case OK:
                    logger.info("===>[{}] created.", curatorEvent.getPath());
                    createProviderZNode(serviceInstance);
                    break;
                case NODEEXISTS:
                    logger.info("===>[{}] already created.", curatorEvent.getPath());
                    createProviderZNode(serviceInstance);
                    break;
            }
        }
    };

    private void createProvidersZNode(final ServiceInstance serviceInstance) throws Exception {
        String serviceName = serviceInstance.getName();
        String path = String.format(SERVICE_PROVIDERS_PATH, serviceName);
        logger.info("===>creating [{}].", path);
        curatorFramework
                .create()
                .withMode(CreateMode.PERSISTENT)
                .inBackground(createProvidersCallback, serviceInstance)
                .forPath(path);
    }

    private BackgroundCallback createConsumersCallback = new BackgroundCallback() {
        @Override
        public void processResult(CuratorFramework curatorFramework,
                                  CuratorEvent curatorEvent) throws Exception {
            switch (Code.get(curatorEvent.getResultCode())){
                case OK:
                    logger.info("===>[{}] created.", curatorEvent.getPath());
                    break;
                case NODEEXISTS:
                    logger.info("===>[{}] already created.", curatorEvent.getPath());
                    break;
            }
        }
    };
    private void createConsumersZNode(final ServiceInstance serviceInstance) throws Exception {
        String serviceName = serviceInstance.getName();
        String path = String.format(SERVICE_CONSUMERS_PATH, serviceName);
        logger.info("===>creating [{}].", path);
        curatorFramework
                .create()
                .withMode(CreateMode.PERSISTENT)
                .inBackground(createConsumersCallback)
                .forPath(path);
    }

    private void createProviderZNode(final ServiceInstance serviceInstance) throws Exception {
        String serviceName = serviceInstance.getName();
        String path = String.format(SERVICE_PROVIDER_PREFIX, serviceName);
        logger.info("===>creating [{}]", path);
        curatorFramework
                .create()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .inBackground()
                .forPath(path, mapper.writeValueAsBytes(serviceInstance));
    }
}
