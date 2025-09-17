package com.seu.vcampus.server.controller;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.common.model.Reservation;
import com.seu.vcampus.server.service.ILibraryService;
import com.seu.vcampus.server.service.LibraryServiceImpl;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.LibraryMessage;
import com.seu.vcampus.common.model.Book;

import java.util.List;
import java.util.Map;

public class LibraryController implements RequestController {
    private ILibraryService libraryService = new LibraryServiceImpl();

    public Message handleRequest(Message request) {
        Message response = new Message();
        response.setType(request.getType());

        try {
            switch (request.getType()) {
                case LibraryMessage.GET_ALL_BOOKS -> {
                    List<Book> books = libraryService.getAllBooks();
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData(books);
                }
                case LibraryMessage.SEARCH_BOOKS -> {
                    String keyword = (String) request.getData();
                    List<Book> books = libraryService.searchBooks(keyword);
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData(books);
                }
                case LibraryMessage.GET_BORROW_BOOKS -> {
                    String UserID = (String) request.getData();
                    List<BorrowRecord> borrowRecords = libraryService.getBorrowRecordsByUserId(UserID);
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData(borrowRecords);

                }
                case LibraryMessage.GET_RESERVATIONS -> {
                    String UserID = (String) request.getData();
                    List<Reservation> reservations = libraryService.getReservationsByUserId(UserID);
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData(reservations);

                }
                case LibraryMessage.ADD_BOOK -> {
                    Book book = (Book) request.getData();

                    boolean success = libraryService.addBook(book);
                    if (success) {
                        response.setStatus(Message.STATUS_SUCCESS);
                        response.setData("增添图书成功");
                    } else {
                        response.setStatus(Message.STATUS_ERROR);
                        response.setData("增添图书失败");
                    }

                }
                case LibraryMessage.DELETE_BOOK -> {
                    String isbn = (String) request.getData();

                    boolean success = libraryService.deleteBook(isbn);
                    if (success) {
                        response.setStatus(Message.STATUS_SUCCESS);
                        response.setData("删除图书成功");
                    } else {
                        response.setStatus(Message.STATUS_ERROR);
                        response.setData("删除图书失败");
                    }

                }
                case LibraryMessage.UPDATE_BOOK -> {
                    Book book = (Book) request.getData();

                    boolean success = libraryService.updateBook(book);
                    if (success) {
                        response.setStatus(Message.STATUS_SUCCESS);
                        response.setData("修改图书成功");
                    } else {
                        response.setStatus(Message.STATUS_ERROR);
                        response.setData("修改图书失败");
                    }

                }
                case LibraryMessage.BORROW_BOOKS -> {

                    Map<String, String> borrowRequest = (Map<String, String>) request.getData();
                    String userId = borrowRequest.get("userId");
                    String isbn = borrowRequest.get("isbn");

                    boolean success = libraryService.borrowBook(userId, isbn);
                    if (success) {
                        response.setStatus(Message.STATUS_SUCCESS);
                        response.setData("借阅成功");
                    } else {
                        response.setStatus(Message.STATUS_ERROR);
                        response.setData("借阅失败：图书不可用或用户已达借阅上限");
                    }
                }
                case LibraryMessage.RESERVE_BOOKS -> {

                    Map<String, String> reserveRequest = (Map<String, String>) request.getData();
                    String userId = reserveRequest.get("userId");
                    String isbn = reserveRequest.get("isbn");

                    boolean success = libraryService.reserveBook(userId, isbn);
                    if (success) {
                        response.setStatus(Message.STATUS_SUCCESS);
                        response.setData("预约成功");
                    } else {
                        response.setStatus(Message.STATUS_ERROR);
                        response.setData("预约失败");
                    }

                }
                case LibraryMessage.RETURN_BOOK -> {
                    Long RecordID = (Long) request.getData();

                    boolean success = libraryService.returnBook(RecordID);
                    if (success) {
                        response.setStatus(Message.STATUS_SUCCESS);
                        response.setData("归还成功");
                    } else {
                        response.setStatus(Message.STATUS_ERROR);
                        response.setData("归还失败");
                    }
                }
                case LibraryMessage.RENEW_BOOK -> {
                    Long RecordID = (Long) request.getData();

                    boolean success = libraryService.renewBook(RecordID);
                    if (success) {
                        response.setStatus(Message.STATUS_SUCCESS);
                        response.setData("续借成功");
                    } else {
                        response.setStatus(Message.STATUS_ERROR);
                        response.setData("续借失败");
                    }

                }
                case LibraryMessage.CANCEL_RESERVATION -> {
                    Long RecordID = (Long) request.getData();

                    boolean success = libraryService.cancelReservation(RecordID);
                    if (success) {
                        response.setStatus(Message.STATUS_SUCCESS);
                        response.setData("取消预约成功");
                    } else {
                        response.setStatus(Message.STATUS_ERROR);
                        response.setData("取消预约失败");
                    }
                }
                case LibraryMessage.GET_BOOKS_BY_ISBN -> {
                    String ISBN = (String) request.getData();
                    Book book = libraryService.getBookByIsbn(ISBN);
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData(book);


                }
                case null, default -> {
                    System.out.println("图书馆未知请求类型：" + request);
                    response.setStatus(Message.STATUS_ERROR);
                    response.setData("未知的请求类型: " + request.getType());
                }
            }
        } catch (Exception e) {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("处理请求时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}