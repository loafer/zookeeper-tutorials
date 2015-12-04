package com.github.loafer.master;

import com.github.loafer.zookeeper.example.Master;

import java.io.IOException;

/**
 * @author zhaojh.
 */
public class MasterOne {
    public static void main(String[] args) throws IOException, InterruptedException {
        Master master = new Master("192.168.56.120:2181/master-workers", "Master One");

        master.startZK();

        master.bootstrap();

        master.runForMaster();

        System.in.read();
        master.stopZK();
    }
}
