package com.seu.vcampus.server.service;


import com.seu.vcampus.common.model.Book;
import java.util.List;

public interface ILibraryService {
    List<Book> getAllBooks();
    List<Book> searchBooks(String keyword);
}