package com.seu.vcampus.server.dao;


import com.seu.vcampus.common.model.Book;
import java.util.List;

public interface IBookDao {
    /**
     * 获取所有图书信息
     *
     * @return 图书列表
     */
    List<Book> getAllBooks();

    /**
     * 根据ISBN查询图书
     *
     * @param isbn 国际标准书号
     * @return 图书对象
     */
    Book getBookByISBN(String isbn);


}

