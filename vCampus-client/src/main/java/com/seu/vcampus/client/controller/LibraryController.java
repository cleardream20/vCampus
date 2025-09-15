package com.seu.vcampus.client.controller;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.client.socket.ClientSocketHandler;
import com.seu.vcampus.common.model.Reservation;
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

    public Book getBookByISBN(String isbn) {
        // 1. 创建请求消息
        Message request = new Message();
        request.setType(LibraryMessage.GETBOOKBYISBN); // 设置消息类型
        request.setData(isbn); // 设置ISBN数据

        try {
            // 2. 发送请求并获取响应
            Message response = socketHandler.sendRequest(request);

            // 3. 检查响应状态
            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                // 4. 成功获取图书信息
                return (Book) response.getData(); // 将响应数据转换为Book对象
            } else {
                // 5. 处理失败情况
                System.err.println("搜索图书失败: " + response.getData());
                return null; // 返回null表示未找到图书
            }
        } catch (Exception e) {
            // 6. 处理网络或序列化异常
            System.err.println("获取图书信息时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
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
    public List<Reservation> getReservationsByUserId(String UserID){
        Message request = new Message();
        request.setType(LibraryMessage.GET_RESERVATIONS);
        request.setData(UserID);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return (List<Reservation>) response.getData();
        } else {
            System.err.println("搜索预约图书列表失败: " + response.getData());
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

    public boolean renewBook(Long RecordID) {

        Message request = new Message();
        request.setType(LibraryMessage.RENEW_BOOK);
        request.setData(RecordID);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return true;
        } else {
            System.err.println("续借失败: " + response.getData());
            return false;
        }
    }

    public boolean cancelReservation(Long reservationID) {

        Message request = new Message();
        request.setType(LibraryMessage.CANCEL_RESERVATION);
        request.setData(reservationID);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return true;
        } else {
            System.err.println("取消预约失败: " + response.getData());
            return false;
        }
    }


    public boolean reserveBook(String userId, String isbn) {
        Map<String, String> reserveRequest = new HashMap<>();
        reserveRequest.put("userId", userId);
        reserveRequest.put("isbn", isbn);

        Message request = new Message();
        request.setType(LibraryMessage.RESERVE_BOOKS);
        request.setData(reserveRequest);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return true;
        } else {
            System.err.println("预约图书失败: " + response.getData());
            return false;
        }
    }


    public boolean addBook(Book book) {
        Message request = new Message();
        request.setType(LibraryMessage.ADD_BOOK);
        request.setData(book);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return true;
        } else {
            System.err.println("增添图书失败: " + response.getData());
            return false;
        }
    }
    public boolean updateBook(Book book) {
        Message request = new Message();
        request.setType(LibraryMessage.UPDATE_BOOK);
        request.setData(book);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return true;
        } else {
            System.err.println("修改图书失败: " + response.getData());
            return false;
        }
    }
    public boolean deleteBook(String isbn) {
        Message request = new Message();
        request.setType(LibraryMessage.DELETE_BOOK);
        request.setData(isbn);

        Message response = socketHandler.sendRequest(request);

        if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
            return true;
        } else {
            System.err.println("删除图书失败: " + response.getData());
            return false;
        }
    }




    public void close() {
        socketHandler.close();
    }
}