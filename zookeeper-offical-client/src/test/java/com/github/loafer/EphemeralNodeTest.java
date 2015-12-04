package com.github.loafer;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author zhaojh.
 */
public class EphemeralNodeTest {
    private static final String CONNECTION_STRING = "192.168.56.120:2181";
    private static final int CONNECTION_TIMEOUT = 3000;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zk = new ZooKeeper(CONNECTION_STRING, CONNECTION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("state: " + event.getState());
                String message = String.format("在节点[%s]上触发事件[%s]", event.getPath(), event.getType());
                System.out.println(message);
            }
        });

        zk.create("/quick4j", "quick4j".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);



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
