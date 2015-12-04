package com.github.loafer.zookeeper.service;

import org.apache.zookeeper.*;

import java.io.IOException;

import static com.github.loafer.zookeeper.service.Constant.*;

/**
 * @author zhaojh.
 */
public class ServiceTwo implements Watcher{
    private static final String SERVICE_NAME = "/serviceTwo";
    private ZooKeeper zk;

    public ServiceTwo() throws IOException {
        zk = new ZooKeeper(CONNECTION_STRING, SESSION_TIMEOUT, this);
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getState() == Event.KeeperState.SyncConnected){
            registSelf();
        }
    }

    private void registSelf(){
        try {
            zk.create(SERVICE_NAME, "service two".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
