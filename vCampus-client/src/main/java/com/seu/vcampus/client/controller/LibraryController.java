package com.seu.vcampus.client.controller;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.client.socket.ClientSocketHandler;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.LibraryMessage;

import java.util.Collections;
import java.util.List;

public class LibraryController {
    private ClientSocketHandler socketHandler;

    public LibraryController() {
        this.socketHandler = new ClientSocketHandler();
    }

    public List<Book> getAllBooks() {
        Message request = new Message();
        request.setType(LibraryMessage.GET_ALL_BOOKS);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return (List<Book>) response.getData();
        } else {
            System.err.println("获取所有图书失败: " + response.getData());
            return Collections.emptyList();
        }
    }

    public List<Book> searchBooks(String keyword) {
        Message request = new Message();
        request.setType(LibraryMessage.SEARCH_BOOKS);
        request.setData(keyword);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return (List<Book>) response.getData();
        } else {
            System.err.println("搜索图书失败: " + response.getData());
            return Collections.emptyList();
        }
    }

    public void close() {
        socketHandler.close();
    }
}