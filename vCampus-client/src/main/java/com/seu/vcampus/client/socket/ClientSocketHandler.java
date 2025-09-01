package com.seu.vcampus.client.socket;

import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSocketHandler extends Thread {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private final BlockingQueue<Message> requestQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Message> responseQueue = new LinkedBlockingQueue<>();

    private static ClientSocketHandler instance;
    private boolean running = true;
    private int reconnectAttempts = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 3;

    private ClientSocketHandler() {
        connectToServer();
    }

    public static synchronized ClientSocketHandler getInstance() {
        if (instance == null) {
            instance = new ClientSocketHandler();
            instance.start();
        }
        return instance;
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("成功连接到服务器");
            reconnectAttempts = 0; // 重置重连计数器
        } catch (IOException e) {
            System.err.println("连接服务器失败: " + e.getMessage());
            handleConnectionError();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                // 处理出站消息
                if (!requestQueue.isEmpty()) {
                    Message request = requestQueue.take();
                    sendMessageInternal(request);
                }

                // 处理入站消息
                if (inputStream != null && socket.getInputStream().available() > 0) {
                    Message response = (Message) inputStream.readObject();
                    responseQueue.put(response);
                }

                Thread.sleep(50); // 避免过度占用CPU
            } catch (IOException e) {
                System.err.println("网络连接异常: " + e.getMessage());
                handleConnectionError();
            } catch (Exception e) {
                System.err.println("通信处理异常: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void sendMessageInternal(Message message) throws IOException {
        if (isConnected()) {
            outputStream.writeObject(message);
            outputStream.flush();
            System.out.println("发送消息: " + message.getType());
        } else {
            System.err.println("无法发送消息 - 连接已断开");
        }
    }

    private void handleConnectionError() {
        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            reconnectAttempts++;
            System.out.println("尝试重连 (尝试" + reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS + ")");
            reconnect();
        } else {
            System.err.println("达到最大重连次数，停止尝试");
            shutdown();
        }
    }

    private void reconnect() {
        try {
            closeConnection();
            Thread.sleep(2000); // 等待2秒重试
            connectToServer();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("重连失败: " + e.getMessage());
        }
    }

    public void sendMessage(Message message) {
        requestQueue.offer(message);
    }

    public Message getResponse() {
        try {
            return responseQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createTimeoutResponse();
        }
    }

    public Message getResponse(long timeoutMillis) {
        try {
            return responseQueue.poll(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createTimeoutResponse();
        }
    }

    private Message createTimeoutResponse() {
        Message message = new Message("TIMEOUT");
        message.setStatus(ResponseCode.SERVICE_UNAVAILABLE);
        message.setDescription("请求超时，无响应");
        return message;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    private void closeConnection() {
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (socket != null) socket.close();
            System.out.println("关闭连接");
        } catch (IOException e) {
            System.err.println("关闭连接时出错: " + e.getMessage());
        }
    }

    public void shutdown() {
        running = false;
        closeConnection();
        instance = null;
        System.out.println("通信处理程序已关闭");
    }
}