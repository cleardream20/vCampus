package com.seu.vcampus.client.controller;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.client.socket.ClientSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class LibraryController {
    private ClientSocketUtil socketHandler;

    public LibraryController() {
        this.socketHandler = new ClientSocketUtil();
    }

    public List<Book> getAllBooks() {
//        Message request = new Message();
//        request.setType(LibraryMessage.GET_ALL_BOOKS);
//
//        Message response = socketHandler.sendRequest(request);
//
//        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
//            return (List<Book>) response.getData();
//        } else {
//            System.err.println("获取所有图书失败: " + response.getData());
//            return Collections.emptyList();
//        }
        return new ArrayList<Book>();
    }

    public List<Book> searchBooks(String keyword) {
//        Message request = new Message();
//        request.setType(LibraryMessage.SEARCH_BOOKS);
//        request.setData(keyword);
//
//        Message response = socketHandler.sendRequest(request);
//
//        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
//            return (List<Book>) response.getData();
//        } else {
//            System.err.println("搜索图书失败: " + response.getData());
//            return Collections.emptyList();
//        }
        return new ArrayList<Book>();
    }

//    public void close() {
//        socketHandler.close();
//    }
}