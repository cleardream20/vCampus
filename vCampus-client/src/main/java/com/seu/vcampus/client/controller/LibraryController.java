package com.seu.vcampus.client.controller;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.client.socket.ClientSocketHandler;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.LibraryMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<BorrowRecord> getBorrowRecordsByUserId (String UserID){
        Message request = new Message();
        request.setType(LibraryMessage.GET_BORROW_BOOKS);
        request.setData(UserID);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return (List<BorrowRecord>) response.getData();
        } else {
            System.err.println("搜索借阅图书列表失败: " + response.getData());
            return Collections.emptyList();
        }
    }

    public boolean borrowBook(String userId, String isbn) {
        // 创建借阅请求数据
        Map<String, String> borrowRequest = new HashMap<>();
        borrowRequest.put("userId", userId);
        borrowRequest.put("isbn", isbn);

        Message request = new Message();
        request.setType(LibraryMessage.BORROW_BOOKS);
        request.setData(borrowRequest);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return true;
        } else {
            System.err.println("借阅图书失败: " + response.getData());
            return false;
        }
    }

    public boolean returnBook(Long RecordID) {

        Message request = new Message();
        request.setType(LibraryMessage.RETURN_BOOK);
        request.setData(RecordID);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return true;
        } else {
            System.err.println("归还图书失败: " + response.getData());
            return false;
        }
    }




    public void close() {
        socketHandler.close();
    }
}