// ClientSocketHandler.java
package com.seu.vcampus.client.socket;

import com.seu.vcampus.common.util.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocketHandler {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientSocketHandler(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}