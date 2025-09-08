package com.seu.vcampus.client.socket;

import com.google.gson.Gson;
import com.seu.vcampus.common.util.Message;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

// 工具类：负责发送请求并接收响应
public class ClientSocketUtil {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    private static final Gson gson = new Gson();

    // 静态方法：发送请求，返回响应
    // 无需一直存在&监听
    public static Message sendRequest(Message request) throws IOException {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT), 5000); // 连接超时
            socket.setSoTimeout(10000); // 读取超时

            try (socket;
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                writer.println(request.toJson());
                String responseLine = reader.readLine();
                if (responseLine == null) {
                    throw new IOException("服务器未返回响应");
                }
                return Message.fromJson(responseLine);
            }
        } catch (IOException e) {
            System.err.println("客户端通信失败: " + e.getMessage());
            throw e; // 向上传播，让业务层处理
        }
    }
}

/*
ClientSocketUtil: send message 工具人
// 1. 构造请求
Message request = new Message("login", userDataJson);

// 2. 发送
Message response = ClientSocketUtil.sendRequest(request);

// 3. 处理
if (response.isSuccess()) {
    User user = User.fromJson(response.getData());
    UserSession.setUser(user);
    mainFrame.showHome();
} else {
    showError(response.getMessage());
}
 */