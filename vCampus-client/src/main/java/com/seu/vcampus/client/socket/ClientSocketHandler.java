package com.seu.vcampus.client.socket;

import com.seu.vcampus.common.util.Message;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientSocketHandler {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    private static final int CONNECTION_TIMEOUT = 10000; // 10秒
    private static final int READ_TIMEOUT = 30000; // 30秒

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private boolean connected = false;

    public ClientSocketHandler() {
        // 延迟连接，不在构造函数中立即连接
    }

    // 添加一个显式的连接方法
    public boolean connect() {
        return connectToServer();
    }

    private boolean connectToServer() {
        try {
            System.out.println("尝试连接到服务器 " + SERVER_HOST + ":" + SERVER_PORT);
            socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(SERVER_HOST, SERVER_PORT), CONNECTION_TIMEOUT);
            socket.setSoTimeout(READ_TIMEOUT);

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush(); // 刷新输出流头
            ois = new ObjectInputStream(socket.getInputStream());
            connected = true;

            System.out.println("已连接到服务器");
            return true;
        } catch (IOException e) {
            System.err.println("连接服务器失败: " + e.getMessage());
            connected = false;
            return false;
        }
    }

    public Message sendRequest(Message request) {
        if (!connected) {
            if (!connectToServer()) {
                return createErrorResponse("无法连接到服务器");
            }
        }

        try {
            System.out.println("发送请求: " + request.getType());

            // 发送请求
            oos.writeObject(request);
            oos.flush();
            System.out.println("请求已发送");

            // 接收响应
            Object responseObj = ois.readObject();

            if (responseObj instanceof Message) {
                Message response = (Message) responseObj;
                System.out.println("收到响应: " + response.getType() + ", 状态: " + response.getStatus());
                return response;
            } else {
                System.err.println("响应格式错误，期望Message对象，收到: " +
                        (responseObj != null ? responseObj.getClass().getName() : "null"));
                return createErrorResponse("服务器响应格式错误: 期望Message对象");
            }
        } catch (EOFException e) {
            System.err.println("EOFException: 服务器可能已关闭连接或响应格式不正确");
            e.printStackTrace();
            connected = false;
            return createErrorResponse("服务器响应格式错误: " + e.getMessage());
        } catch (SocketTimeoutException e) {
            System.err.println("请求超时: " + e.getMessage());
            connected = false;
            return createErrorResponse("请求超时: " + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("发送请求失败: " + e.getMessage());
            e.printStackTrace();
            connected = false;
            return createErrorResponse("网络错误: " + e.getMessage());
        }
    }

    private Message createErrorResponse(String message) {
        Message response = new Message();
        response.setType("ERROR");
        response.setStatus(Message.STATUS_ERROR);
        response.setData(message);
        return response;
    }

    public void close() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            connected = false;
            System.out.println("已断开与服务器的连接");
        } catch (IOException e) {
            System.err.println("关闭连接时发生错误: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected;
    }
}