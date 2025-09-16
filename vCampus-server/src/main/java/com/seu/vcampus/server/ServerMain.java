package com.seu.vcampus.server;

import com.seu.vcampus.server.socket.ServerSocketThread;

import java.util.Scanner;

public class ServerMain {
    private static ServerSocketThread server;

    public static void main(String[] args) {
        server = new ServerSocketThread();
        server.start();
        System.out.println("虚拟校园系统服务端已启动，监听端口: 8888");

        // 添加控制台命令处理
        handleConsoleCommands();
    }

    /**
     * 处理控制台命令
     */
    private static void handleConsoleCommands() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n请输入命令 (输入 'help' 查看可用命令):");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "status":
                    System.out.println("服务器状态: 运行中");
                    break;
                case "stop":
                    shutdownServer();
                    scanner.close();
                    return;
                default:
                    System.out.println("未知命令: " + command);
                    break;
            }
        }
    }

    /**
     * 打印帮助信息
     */
    private static void printHelp() {
        System.out.println("可用命令:");
        System.out.println("  help    - 显示帮助信息");
        System.out.println("  status  - 显示服务器状态");
        System.out.println("  stop    - 关闭服务器");
    }

    /**
     * 关闭服务器
     */
    private static void shutdownServer() {
        System.out.println("正在关闭服务器...");

        if (server != null) {
            server.stopServer();
        }

        System.out.println("服务器已关闭");
    }
}