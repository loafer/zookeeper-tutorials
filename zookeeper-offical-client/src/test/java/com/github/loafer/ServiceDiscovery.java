package com.github.loafer;

import static com.github.loafer.zookeeper.service.Constant.*;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

/**
 * @author zhaojh.
 */
public class ServiceDiscovery implements Watcher{
    private ZooKeeper zooKeeper;
    private boolean monitoring;

    public ServiceDiscovery() throws IOException {
        zooKeeper = new ZooKeeper(CONNECTION_STRING, SESSION_TIMEOUT, this);
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getState() == Event.KeeperState.SyncConnected && ! monitoring){
            watchServiceZnode();
            monitoring = true;
        }

        if(event.getType() == Event.EventType.NodeChildrenChanged){
            watchServiceZnode();
            System.out.println(1);
        }
    }

    private void watchServiceZnode(){
        try {
            List<String> serviceList = zooKeeper.getChildren("/", this);
            for (String service : serviceList){
                System.out.println(service);
            }
            System.out.println("================");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
