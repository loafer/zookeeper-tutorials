package com.github.loafer.workers;

import com.github.loafer.zookeeper.example.Worker;

/**
 * @author zhaojh.
 */
public class Workers {
    public static void main(String[] args){
        for (int i=1; i<=2; i++){
            Thread thread = new Thread(new Worker("192.168.56.120:2181/master-workers/workers"));
            thread.start();
        }
    }
}
