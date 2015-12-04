package com.github.loafer;

import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhaojh.
 */
public class ExampleTest {
    private CountDownLatch downLatch = new CountDownLatch(1);
    private ZooKeeper zk;
    private Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            System.out.println(event.toString());
            if(event.getState() == Event.KeeperState.SyncConnected){
                downLatch.countDown();
            }
        }
    };

    @Before
    public void setup() throws IOException, InterruptedException {
        zk = new ZooKeeper("192.168.56.120:2181", 5000, watcher);
        downLatch.await();
    }

//    @Test
    public void testExists() throws KeeperException, InterruptedException, IOException {
        zk.exists("/loafer", true);
        zk.close();

    }

//    @Test
    public void testEphemeral() throws KeeperException, InterruptedException, IOException {
        zk.create("/quick4j", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.create("/quick4j/helloService", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        zk.create("/quick4j/helloService/providers", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        System.in.read();
        zk.close();
    }
}
