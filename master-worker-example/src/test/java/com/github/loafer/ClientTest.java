package com.github.loafer;

import com.github.loafer.zookeeper.example.Client;

import java.io.IOException;

/**
 * @author zhaojh.
 */
public class ClientTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client("192.168.56.120:2181/master-workers/tasks");
        client.startZK();

        client.queueCommand("cmd");
        System.in.read();
        client.stopZK();
    }
}
