package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.common.model.Book;
import java.util.List;

public interface ILibraryService {
    List<Book> getAllBooks();
    List<Book> searchBooks(String keyword);

    Book getBookByIsbn(String isbn);
    // 借阅管理相关方法
    List<BorrowRecord> getBorrowRecordsByUserId(String userId);
    boolean borrowBook(String userId, String isbn);
    boolean returnBook(Long recordId);
    boolean renewBook(Long recordId);
}