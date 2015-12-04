package com.github.loafer.zookeeper.service;

import static com.github.loafer.zookeeper.service.Constant.*;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @author zhaojh.
 */
public class ServiceOne implements Watcher{
    private static final String SERVICE_NAME = "/serviceOne";
    private ZooKeeper zk;

    public ServiceOne() throws IOException {
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
            zk.create(SERVICE_NAME, "service one".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
