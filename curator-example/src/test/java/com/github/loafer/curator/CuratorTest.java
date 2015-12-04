package com.github.loafer.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;

/**
 * @author zhaojh.
 */
public class CuratorTest {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.56.120:2181", retryPolicy);

        ConnectionStateListener stateListener = new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if(connectionState == ConnectionState.CONNECTED){
                    System.out.println("connection.");
                }
            }
        };
        client.getConnectionStateListenable().addListener(stateListener);


        UnhandledErrorListener errorListener = new UnhandledErrorListener() {
            @Override
            public void unhandledError(String s, Throwable throwable) {
                System.out.println("===>"+s);
            }
        };
        client.getUnhandledErrorListenable().addListener(errorListener);


        client.start();
        client.blockUntilConnected();
        System.out.println("bala~~bala~~");

        client.create().withMode(CreateMode.PERSISTENT).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
//                switch (Code.get(curatorEvent.getResultCode())) {
//                    case NODEEXISTS:
//                        System.out.println(curatorEvent.getPath() + " already created.");
//                        break;
//                }

                switch (curatorEvent.getType()){
                    case CREATE:
                        System.out.println(curatorEvent.getPath() + " already created.");
                        break;
                }
            }
        }).forPath("/curator");

        System.out.println("wait");
        Thread.sleep(5000);
        System.out.println("wakeup");

        client.create().withMode(CreateMode.PERSISTENT).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                switch (Code.get(curatorEvent.getResultCode())) {
                    case NODEEXISTS:
                        System.out.println(curatorEvent.getPath() + " already created.");
                        break;
                }
            }
        }).forPath("/curator/clients");
        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/curator/clients/client-");
        System.in.read();
        client.close();
    }
}
