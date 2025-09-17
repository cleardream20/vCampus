package com.seu.vcampus.client.service;

import com.seu.vcampus.client.socket.ClientSocketUtil;
import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.common.model.Reservation;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.LibraryMessage;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryService{

    public LibraryService() {
    }

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
            System.err.println("获取所有图书请求异常: " +  e.getMessage());
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
            System.err.println("搜索图书请求异常: " + e.getMessage());
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
                return (Book) response.getData();
            } else {
                System.err.println("搜索图书失败: " + response.getData());
                return null;
            }
        } catch (IOException e) {
            System.err.println("获取图书信息请求异常: " + e.getMessage());
        }
        return null;
    }

    public List<BorrowRecord> getBorrowRecordsByUserId(String UserID) {
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
            System.err.println("获取借阅记录请求异常: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public List<Reservation> getReservationsByUserId(String UserID) {
        Message request = new Message();
        request.setType(LibraryMessage.GET_RESERVATIONS);
        request.setData(UserID);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return (List<Reservation>) response.getData();
            } else {
                System.err.println("搜索预约图书列表失败: " + response.getData());
                return Collections.emptyList();
            }
        } catch (IOException e) {
            System.err.println("获取预约记录请求异常: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public boolean borrowBook(String userId, String isbn) {
        Map<String, String> borrowRequest = new HashMap<>();
        borrowRequest.put("userId", userId);
        borrowRequest.put("isbn", isbn);

        Message request = new Message();
        request.setType(LibraryMessage.BORROW_BOOKS);
        request.setData(borrowRequest);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("借阅图书失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("借阅图书请求异常: " + e.getMessage());
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
            System.err.println("归还图书请求异常: " + e.getMessage());
        }
        return false;
    }

    public boolean renewBook(Long RecordID) {
        Message request = new Message();
        request.setType(LibraryMessage.RENEW_BOOK);
        request.setData(RecordID);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("续借失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("续借请求异常: " + e.getMessage());
        }
        return false;
    }

    public boolean cancelReservation(Long reservationID) {
        Message request = new Message();
        request.setType(LibraryMessage.CANCEL_RESERVATION);
        request.setData(reservationID);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("取消预约失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("取消预约请求异常: " + e.getMessage());
        }
        return false;
    }

    public boolean reserveBook(String userId, String isbn) {
        Map<String, String> reserveRequest = new HashMap<>();
        reserveRequest.put("userId", userId);
        reserveRequest.put("isbn", isbn);

        Message request = new Message();
        request.setType(LibraryMessage.RESERVE_BOOKS);
        request.setData(reserveRequest);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("预约图书失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("预约图书请求异常: " + e.getMessage());
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
            System.err.println("增添图书请求异常: " + e.getMessage());
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
            System.err.println("修改图书请求异常: " + e.getMessage());
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
            System.err.println("删除图书请求异常: " + e.getMessage());
        }
        return false;
    }
}