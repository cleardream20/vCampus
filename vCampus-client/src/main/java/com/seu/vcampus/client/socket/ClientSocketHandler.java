package com.seu.vcampus.client.socket;
import com.seu.vcampus.common.util.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocketHandler {
//    private static final String SERVER_HOST = "192.168.30.2";
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ClientSocketHandler() {
        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("已连接到服务器");
        } catch (IOException e) {
            System.err.println("连接服务器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Message sendRequest(Message request) {
        try {
            if (socket == null || socket.isClosed()) {
                connectToServer();
            }

            // 发送请求
            oos.writeObject(request);
            oos.flush();

            // 接收响应
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("发送请求失败: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("网络错误: " + e.getMessage());
        }
    }

    private Message createErrorResponse(String message) {
        Message response = new Message();
        response.setStatus(Message.STATUS_ERROR);
        response.setData(message);
        return response;
    }

    public void close() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}