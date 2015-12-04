package com.github.loafer.zookeeper.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author zhaojh.
 */
public class Client implements Watcher {
    private static Logger logger = LoggerFactory.getLogger(Client.class);
    private ZooKeeper zk;
    private String hostport;

    public Client(String hostport) {
        this.hostport = hostport;
    }

    @Override
    public void process(WatchedEvent event) {

    }

    public void startZK() throws IOException {
        zk = new ZooKeeper(hostport, 2000, this);
    }

    public void stopZK() throws InterruptedException {
        zk.close();
    }

    public void queueCommand(String command){
        zk.create(
                "/task-",
                command.getBytes(),
                Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL,
                createTaskCallback,
                command
        );
    }

    private StringCallback createTaskCallback = new StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (Code.get(rc)){
                case OK:
                    logger.info("===>{} created. To deal with the [{}]", name, ctx.toString());
                    break;
            }
        }
    };
}
