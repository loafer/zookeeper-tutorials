package com.github.loafer;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author zhaojh.
 */
public class WatchZnodeTest {
    private static final String CONNECTION_STRING = "192.168.56.120:2181";
    private static final int CONNECTION_TIMEOUT = 3000;
    private static final String ZK_SERVICE_REGISTRY_PATH = "/serviceRegistry";

    private static void watchServiceRegistryZnode(final ZooKeeper zk){
        try {
            zk.exists(ZK_SERVICE_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    String message = String.format("在节点[%s]上触发事件[%s]", event.getPath(), event.getType());
                    System.out.println(message);

                    watchServiceRegistryZnode(zk);
                }
            });
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        final ZooKeeper zk = new ZooKeeper(CONNECTION_STRING, CONNECTION_TIMEOUT, null);

        zk.create(ZK_SERVICE_REGISTRY_PATH, "serviceRegistry".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        watchServiceRegistryZnode(zk);

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入'quit'结束程序:");
        while (true){
            String commond = scanner.nextLine();
            if(commond.equalsIgnoreCase("quit")){
                System.out.println("bye!");
                break;
            }
            System.out.println(">>" + commond);
        }

        zk.close();
    }
}
