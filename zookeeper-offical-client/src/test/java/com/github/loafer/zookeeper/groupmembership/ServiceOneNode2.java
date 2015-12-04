package com.github.loafer.zookeeper.groupmembership;

import com.github.loafer.zookeeper.service.ServiceOne;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author zhaojh.
 */
public class ServiceOneNode2 {
    public static void main(String[] args) throws IOException {
        ServiceOne serviceOne = new ServiceOne();

        Scanner scanner = new Scanner(System.in);
        System.out.println("[ServiceOneNode1]请输入'quit'结束程序:");
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
