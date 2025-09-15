package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.common.model.Reservation;
import com.seu.vcampus.server.dao.BookDaoImpl;
import com.seu.vcampus.server.dao.IBookDao;

import java.util.*;
import java.util.stream.Collectors;

public class LibraryServiceImpl implements ILibraryService {
    // 模拟的图书数据
    private List<Book> books = new ArrayList<>();
    private List<BorrowRecord> borrowRecords =new ArrayList<>();
    private List<Reservation> reservations =new ArrayList<>();
    private BookDaoImpl bookDao= new BookDaoImpl();


    public LibraryServiceImpl() {
        // 初始化模拟数据
        initializeMockData();
    }

    private void initializeMockData() {
//        books.add(new Book("9787111636665", "Java核心技术", "Cay S. Horstmann", "机械工业出版社", 2020, 10, 7, "A区3排","D:\\idea_project\\vCampus\\Images\\default_book.jpg"));
//        books.add(new Book("9787302518383", "Python编程", "Mark Lutz", "中国电力出版社", 2019, 5, 0, "B区5排","D:\\idea_project\\vCampus\\Images\\default_book.jpg"));
//        books.add(new Book("9787115537977", "深入理解计算机系统", "Randal E.Bryant", "机械工业出版社", 2021, 8, 5, "C区2排","D:\\idea_project\\vCampus\\Images\\default_book.jpg"));
//        books.add(new Book("9787121382061", "算法导论", "Thomas H.Cormen", "电子工业出版社", 2020, 15, 12, "A区1排","D:\\idea_project\\vCampus\\Images\\default_book.jpg"));
//        books.add(new Book("9787115480655", "数据库系统概念", "Abraham Silberschatz", "机械工业出版社", 2019, 6, 4, "B区3排","D:\\idea_project\\vCampus\\Images\\default_book.jpg"));
        addBooks();
        addBorrowRecords();
        addReservations();
    }
    private void addBooks(){
        books=bookDao.getAllBooks();
    }

    private void addBorrowRecords() {
       borrowRecords=bookDao.getBorrowRecordsByUserId("20210001");
    }

    private  void addReservations(){
        reservations=bookDao.getReservationsByUserId("20210001");
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


    @Override
    public List<BorrowRecord> getBorrowRecordsByUserId(String userId) {
        // 获取用户当前在借的图书列表
        return bookDao.getBorrowRecordsByUserId(userId);
    }

    @Override
    public List<Reservation> getReservationsByUserId(String userId){
        return bookDao.getReservationsByUserId(userId);
    }



    // 借阅图书
    @Override
    public boolean borrowBook(String userId, String isbn) {
       return bookDao.borrowBook(userId,isbn);
    }


    // 归还图书
    @Override
    public boolean returnBook(Long recordId) {
        return bookDao.returnBook(recordId);
    }
    @Override
    public boolean cancelReservation(Long recordId) {
        return bookDao.cancelReservation(recordId);
    }

    @Override
    public boolean reserveBook(String userId, String isbn) {
        return bookDao.reserveBook(userId,isbn);
    }

    // 续借图书
    @Override
    public boolean renewBook(Long recordId) {
       return bookDao.renewBook(recordId);
    }

    @Override
    public List<Book> getAllBooks() {
        books=bookDao.getAllBooks();
        return books;
    }

    @Override
    public boolean addBook(Book book) {
        books.add(book);
        return bookDao.addBook(book);
    }

    @Override
    public boolean deleteBook(String isbn) {
        return bookDao.deleteBook(isbn);

    }




    @Override
    public Book getBookByIsbn(String isbn) {
        return bookDao.getBookByISBN(isbn);
    }

    @Override
    public boolean updateBook(Book book) {
        return bookDao.updateBook(book);
    }


}
