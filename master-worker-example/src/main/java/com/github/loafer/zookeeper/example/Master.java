package com.github.loafer.zookeeper.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.KeeperException.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author zhaojh.
 */
public class Master implements Watcher {
    private static Logger logger = LoggerFactory.getLogger(Master.class);
    private String hostport;
    private String masterName;
    private ZooKeeper zk;
    private boolean leader;

    public Master(String hostport, String masterName) {
        this.hostport = hostport;
        this.masterName = masterName;
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.toString());
    }

    public void startZK() throws IOException {
        zk = new ZooKeeper(hostport, 2000, this);
    }

    public void stopZK() throws InterruptedException {
        zk.close();
    }

    public void runForMaster(){
        zk.create(
                "/master",
                masterName.getBytes(),
                Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL,
                creatMasterCallback,
                null
        );
    }

    public void bootstrap(){
        createParentNode("/workers", new byte[0]);
        createParentNode("/assign", new byte[0]);
        createParentNode("/tasks", new byte[0]);
        createParentNode("/status", new byte[0]);
    }

    private void createParentNode(String path, byte[] data){
        zk.create(
            path,
            data,
            Ids.OPEN_ACL_UNSAFE,
            CreateMode.PERSISTENT,
            createParentNodeCallback,
            data
        );
    }

    private StringCallback createParentNodeCallback = new StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (Code.get(rc)){
                case CONNECTIONLOSS:
                    createParentNode(path, (byte[]) ctx);
                    break;
                case OK:
                    logger.info("===> [{}]{} created.", masterName, path);
                    break;
                case NODEEXISTS:
                    logger.info("===> [{}]{} already registered.", masterName, path);
                    break;
                default:
                    logger.info("===> [{}]Something went wrong.");
            }
        }
    };

    private void checkMaster(){
        zk.getData("/master", true, checkMasterCallback, null);
    }

    private DataCallback checkMasterCallback = new DataCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            switch (Code.get(rc)){
                case CONNECTIONLOSS:
                    checkMaster();
                    break;
                case NONODE:
                    runForMaster();
                    break;
            }
        }
    };

    private void watchMaster(){
        zk.exists(
            "/master",
            masterWatcher,
            masterExistsCallback,
            null
        );
    }

    private Watcher masterWatcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            switch (event.getType()){
                case NodeDeleted:
                    runForMaster();
                    break;
            }
        }
    };

    private StatCallback masterExistsCallback = new StatCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            switch (Code.get(rc)){
                case CONNECTIONLOSS:
                    watchMaster();
                    break;
                case OK:
                    if(stat == null){
                        runForMaster();
                    }
                    break;
                default:
                    checkMaster();
                    break;
            }
        }
    };

    private void getWorkers(){
        zk.getChildren(
                "/workers",
                workersChangeWatcher,
                workersGetChildrenCallback,
                null
        );
    }

    private Watcher workersChangeWatcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            switch (event.getType()){
                case NodeChildrenChanged:
                    getWorkers();
                    break;
            }
        }
    };

    private ChildrenCallback workersGetChildrenCallback = new ChildrenCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children) {
            switch (Code.get(rc)){
                case CONNECTIONLOSS:
                    getWorkers();
                    break;
                case OK:
                    //分配worker
                    logger.info("===> Successfully got a list of workers: {} workers.", children.size());
                    break;
                default:
                    logger.info("===> get Workers fail.");
                    break;
            }
        }
    };

    private StringCallback creatMasterCallback = new StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (Code.get(rc)){
                case CONNECTIONLOSS:
                    checkMaster();
                    break;
                case OK:
                    leader = true;
                    logger.info("===> [{}] {} created.", masterName, name);
                    getWorkers();
                    break;
                case NODEEXISTS:
                    watchMaster();
                    break;
                default:
                    leader = false;
            }

            logger.info("===> [{}] {}", masterName, Code.get(rc));
            if(leader){
                logger.info("===> [{}] I'm the leader.", masterName);
            }else{
                logger.info("===> [{}] Someone else is the leader. I'm backup", masterName);
            }
        }
    };


    public static void main(String[] args) throws IOException, InterruptedException {
        Master master = new Master("192.168.56.120:2181/master-workers", "Master One");
        master.startZK();
        Thread.sleep(1000);
        master.runForMaster();

        System.in.read();
        master.stopZK();
    }
}
