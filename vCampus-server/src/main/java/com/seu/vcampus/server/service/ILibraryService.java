package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.Reservation;

import java.util.List;

public interface ILibraryService {
    List<Book> getAllBooks();
    List<Book> searchBooks(String keyword);

    Book getBookByIsbn(String isbn);
    // 借阅管理相关方法
    List<BorrowRecord> getBorrowRecordsByUserId(String userId);
    List<Reservation> getReservationsByUserId(String userId);
    boolean borrowBook(String userId, String isbn);
    boolean returnBook(Long recordId);
    boolean cancelReservation(Long recordId);
    boolean renewBook(Long recordId);
    boolean reserveBook(String userId, String isbn);
    boolean addBook(Book book);
    boolean deleteBook(String isbn);
    boolean updateBook(Book book);

}