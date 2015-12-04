package com.github.loafer.zookeeper.groupmembership;

import com.github.loafer.zookeeper.service.ServiceOne;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author zhaojh.
 */
public class ServiceOneClusterTest {

    public static void main(String[] args) throws IOException {
        ServiceOne serviceOneNode1 = new ServiceOne();
        ServiceOne serviceOneNode2 = new ServiceOne();
        ServiceOne serviceOneNode3 = new ServiceOne();



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
    }
}
