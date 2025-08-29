package com.seu.vcampus.server.service;



import com.seu.vcampus.common.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryServiceImpl implements ILibraryService {
    // 模拟的图书数据
    private List<Book> books = new ArrayList<>();

    public LibraryServiceImpl() {
        // 初始化模拟数据
        initializeMockData();
    }

    private void initializeMockData() {
        books.add(new Book("9787111636665", "Java核心技术", "Cay S. Horstmann", "机械工业出版社", 2020, 10, 7, "A区3排"));
        books.add(new Book("9787302518383", "Python编程", "Mark Lutz", "中国电力出版社", 2019, 5, 0, "B区5排"));
        books.add(new Book("9787115537977", "深入理解计算机系统", "Randal E.Bryant", "机械工业出版社", 2021, 8, 5, "C区2排"));
        books.add(new Book("9787121382061", "算法导论", "Thomas H.Cormen", "电子工业出版社", 2020, 15, 12, "A区1排"));
        books.add(new Book("9787115480655", "数据库系统概念", "Abraham Silberschatz", "机械工业出版社", 2019, 6, 4, "B区3排"));
    }

    @Override
    public List<Book> getAllBooks() {
        return new ArrayList<>(books); // 返回副本
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }

        String lowerKeyword = keyword.toLowerCase();
        return books.stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(lowerKeyword) ||
                                book.getAuthor().toLowerCase().contains(lowerKeyword) ||
                                book.getIsbn().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }
}
