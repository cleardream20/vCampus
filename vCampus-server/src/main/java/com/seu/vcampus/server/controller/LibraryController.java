package com.seu.vcampus.server.controller;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.server.service.ILibraryService;
import com.seu.vcampus.server.service.LibraryServiceImpl;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.LibraryMessage;
import com.seu.vcampus.common.model.Book;

import java.util.List;
import java.util.Map;

public class LibraryController {
    private ILibraryService libraryService = new LibraryServiceImpl();

    public Message handleRequest(Message request) {
        Message response = new Message();
        response.setType(request.getType());

        try {
            if (LibraryMessage.GET_ALL_BOOKS.equals(request.getType())) {
                List<Book> books = libraryService.getAllBooks();
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(books);
            } else if (LibraryMessage.SEARCH_BOOKS.equals(request.getType())) {
                String keyword = (String) request.getData();
                List<Book> books = libraryService.searchBooks(keyword);
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(books);
            } else if (LibraryMessage.GET_BORROW_BOOKS.equals(request.getType())) {
                String UserID = (String) request.getData();
                List<BorrowRecord> borrowRecords = libraryService.getBorrowRecordsByUserId(UserID);
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(borrowRecords);


            } else if (LibraryMessage.ADD_BOOK.equals(request.getType())) {
                Book book=(Book) request.getData();

                boolean success = libraryService.addBook(book);
                if (success) {
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData("增添图书成功");
                } else {
                    response.setStatus(Message.STATUS_ERROR);
                    response.setData("增添图书失败");
                }


            } else if (LibraryMessage.DELETE_BOOK.equals(request.getType())) {
                String isbn=(String) request.getData();

                boolean success = libraryService.deleteBook(isbn);
                if (success) {
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData("删除图书成功");
                } else {
                    response.setStatus(Message.STATUS_ERROR);
                    response.setData("删除图书失败");
                }

            } else if (LibraryMessage.BORROW_BOOKS.equals(request.getType())) {
                 //填补
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

            } else if (LibraryMessage.RETURN_BOOK.equals(request.getType())) {
                Long RecordID = (Long) request.getData();

                boolean success = libraryService.returnBook(RecordID);
                if (success) {
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData("归还成功");
                } else {
                    response.setStatus(Message.STATUS_ERROR);
                    response.setData("归还失败");
                }


            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("未知的请求类型: " + request.getType());
            }
        } catch (Exception e) {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("处理请求时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}