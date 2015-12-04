package com.github.loafer.zookeeper.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author zhaojh.
 */
public class Worker implements Watcher, Runnable {
    private static Logger logger = LoggerFactory.getLogger(Worker.class);
    private ZooKeeper zk;
    private String hostport;

    public Worker(String hostport) {
        this.hostport = hostport;
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.toString());
    }

    @Override
    public void run() {
        try {
            startZK();
            register();
            System.in.read();
            stopZK();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startZK() throws IOException {
        zk = new ZooKeeper(hostport, 2000, this);
    }

    public void stopZK() throws InterruptedException {
        zk.close();
    }

    public void register(){
        zk.create(
                "/worker-",
                "Idle".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL,
                createWorkerCallback,
                null
        );
    }

    private StringCallback createWorkerCallback = new StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (Code.get(rc)){
                case CONNECTIONLOSS:
                    register();
                    break;
                case OK:
                    logger.info("===>[Worker]{} created.", name);
                    break;
                case NODEEXISTS:
                    logger.info("===>[Worker]{} already registered.", name);
                    break;
                default:
                    logger.info("===>[Worker]Something went wrong.");
                    break;
            }
        }
    };
}
