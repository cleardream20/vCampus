package com.seu.vcampus.client.service;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.client.socket.ClientSocketUtil;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.LibraryMessage;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryService {

    public LibraryService() {}


    public List<Book> getAllBooks() {
        Message request = new Message();
        request.setType(LibraryMessage.GET_ALL_BOOKS);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return (List<Book>) response.getData();
            } else {
                System.err.println("获取所有图书失败: " + response.getData());
                return Collections.emptyList();
            }
        } catch (IOException e) {
            System.err.println("获取图书请求失败: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public List<Book> searchBooks(String keyword) {
        Message request = new Message();
        request.setType(LibraryMessage.SEARCH_BOOKS);
        request.setData(keyword);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return (List<Book>) response.getData();
            } else {
                System.err.println("搜索图书失败: " + response.getData());
                return Collections.emptyList();
            }
        } catch (IOException e) {
            System.err.println("搜索图书请求失败: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public Book getBookByISBN(String isbn) {
        Message request = new Message();
        request.setType(LibraryMessage.GET_BOOKS_BY_ISBN);
        request.setData(isbn);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return (Book) response.getData(); // 将响应数据转换为Book对象
            } else {
                System.err.println("搜索图书失败: " + response.getData());
                return null;
            }
        } catch (IOException e) {
            System.err.println("搜索图书请求错误: " + e.getMessage());
            return null;
        }
    }

    public List<BorrowRecord> getBorrowRecordsByUserId (String UserID){
        Message request = new Message();
        request.setType(LibraryMessage.GET_BORROW_BOOKS);
        request.setData(UserID);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return (List<BorrowRecord>) response.getData();
            } else {
                System.err.println("搜索借阅图书列表失败: " + response.getData());
                return Collections.emptyList();
            }
        } catch (IOException e) {
            System.err.println("搜索借阅图书列表请求失败: " + e.getMessage());
        }
        return Collections.emptyList();
    }


    public boolean borrowBook(String userId, String isbn) {
        // 创建借阅请求数据
        Map<String, String> borrowRequest = new HashMap<>();
        borrowRequest.put("userId", userId);
        borrowRequest.put("isbn", isbn);

        try {
            Message request = new Message();
            request.setType(LibraryMessage.BORROW_BOOKS);
            request.setData(borrowRequest);

            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("借阅图书失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("借阅图书请求失败: " + e.getMessage());
        }
        return false;
    }

    public boolean returnBook(Long RecordID) {

        Message request = new Message();
        request.setType(LibraryMessage.RETURN_BOOK);
        request.setData(RecordID);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("归还图书失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("归还图书请求失败: " + e.getMessage());
        }
        return false;
    }

    public boolean addBook(Book book) {
        Message request = new Message();
        request.setType(LibraryMessage.ADD_BOOK);
        request.setData(book);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("增添图书失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("增添图书请求失败: " + e.getMessage());
        }
        return false;
    }

    public boolean updateBook(Book book) {
        Message request = new Message();
        request.setType(LibraryMessage.UPDATE_BOOK);
        request.setData(book);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("修改图书失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("修改图书请求失败: " + e.getMessage());
        }
        return false;
    }
    public boolean deleteBook(String isbn) {
        Message request = new Message();
        request.setType(LibraryMessage.DELETE_BOOK);
        request.setData(isbn);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("删除图书失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("删除图书请求失败: " + e.getMessage());
        }
        return false;
    }
}