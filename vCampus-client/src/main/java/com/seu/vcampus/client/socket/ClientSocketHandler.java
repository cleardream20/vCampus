package com.seu.vcampus.client.socket;

import com.seu.vcampus.common.util.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocketHandler {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

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
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            oos = new ObjectOutputStream(socket.getOutputStream());
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
            // 发送请求
            oos.writeObject(request);
            oos.flush();

            // 接收响应
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("发送请求失败: " + e.getMessage());
            connected = false; // 标记为断开连接
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