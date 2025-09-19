package com.seu.vcampus.server.dao;


import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.common.model.Reservation;

import java.util.List;

public interface IBookDao {
    /**
     * 获取所有图书信息
     *
     * @return 图书列表
     */
    List<Book> getAllBooks();
    List<Book> searchBooks(String keyword);
    /**
     * 根据ISBN查询图书
     *
     * @param isbn 国际标准书号
     * @return 图书对象
     */
    Book getBookByISBN(String isbn);
    /**
     * 添加新书
     *
     * @param book 图书对象
     * @return 添加是否成功
     */
    boolean addBook(Book book);

    /**
     * 删除图书
     *
     * @param isbn 国际标准书号
     * @return 删除是否成功
     */
    boolean deleteBook(String isbn);
    /**
     * 修改书
     *
     * @param book 图书对象
     * @return 修改是否成功
     */
    boolean updateBook(Book book);

    List<BorrowRecord> getBorrowRecordsByUserId(String userId);

    boolean borrowBook(String userId, String isbn);
    boolean canUserBorrow(String userId);
    boolean returnBook(Long recordId);
    boolean cancelReservation(Long recordId);
    List<Reservation> getReservationsByUserId(String userId);
    boolean reserveBook(String userId, String isbn);
    boolean renewBook(long recordId);
}