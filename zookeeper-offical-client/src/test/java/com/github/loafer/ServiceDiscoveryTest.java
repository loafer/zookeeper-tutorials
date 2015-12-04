package com.github.loafer;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author zhaojh.
 */
public class ServiceDiscoveryTest {
    public static void main(String[] args) throws IOException {
        ServiceDiscovery discovery = new ServiceDiscovery();

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
