package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.server.dao.IBookDao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements IBookDao {
    private final String DB_URL = "jdbc:ucanaccess://D:\\idea_project\\vCampus\\database\\vCampus.accdb;" +
            "immediatelyrelease=true;" + // 添加此参数
            "charset=GBK;" +
            "ignorecase=true";
    private Connection connection;
    List<Book> books=new ArrayList<>();
    int num;
    public BookDaoImpl() {
        books= getAllBooks();
        num=books.size();
        connect();
        outputAllBooks();
    }

    private void connect() {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        }
    }

    private void outputAllBooks() {
        List<Book> books = getAllBooks();
        if (books.isEmpty()) {
            System.out.println("数据库中没有图书数据");
            return;
        }

        System.out.println("数据库中的所有图书数据:");
        System.out.println("======================================================================================================================================");
        System.out.printf("%-15s %-30s %-20s %-20s %-10s %-8s %-8s %-15s %-20s%n",
                "ISBN", "书名", "作者", "出版社", "出版年份", "总数量", "可借量", "位置", "图片路径");
        System.out.println("======================================================================================================================================");

        for (Book book : books) {
            System.out.printf("%-15s %-30s %-20s %-20s %-10d %-8d %-8d %-15s %-20s%n",
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor() != null ? book.getAuthor() : "N/A",
                    book.getPublisher() != null ? book.getPublisher() : "N/A",
                    book.getPublishYear(),
                    book.getTotalCopies(),
                    book.getAvailableCopies(),
                    book.getLocation() != null ? book.getLocation() : "N/A",
                    book.getImagePath() != null ? book.getImagePath() : "N/A");
        }
        System.out.println("======================================================================================================================================");
        System.out.println("共找到 " + books.size() + " 本图书");
    }

    private void ensureConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("数据库连接已关闭，尝试重新连接...");
                connect();
            }
        } catch (SQLException e) {
            System.err.println("检查连接状态失败: " + e.getMessage());
        }
    }

    @Override
    public List<Book> getAllBooks() {
        ensureConnection();
        List<Book> books = new ArrayList<>();
        String sql = "SELECT bIsbn, bTitle, bAuthor, bPublisher, bPublishYear, " +
                "bTotal, bAvailable, bLocation, bImagePath " +
                "FROM tblBook";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有图书失败: " + e.getMessage());
        }
        return books;
    }

    @Override
    public Book getBookByISBN(String isbn) {
        String sql = "SELECT bIsbn, bTitle, bAuthor, bPublisher, bPublishYear, " +
                "bTotal, bAvailable, bLocation, bImagePath " +
                "FROM tblBook WHERE bIsbn = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("按ISBN查询失败: " + e.getMessage());
        }
        return null;
    }


    @Override
    public boolean addBook(Book book) {
        String sql = "INSERT INTO tblBook (ID,bIsbn, bTitle, bAuthor, bPublisher, " +
                "bPublishYear, bTotal, bAvailable, bLocation, bImagePath) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        System.out.println(book.toString());
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            num+=1;
            setBookParameters(pstmt, book);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("增添图书失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteBook(String isbn) {
        String sql = "DELETE FROM tblBook WHERE bIsbn = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            num-=1;
            pstmt.setString(1, isbn);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("删除图书失败: " + e.getMessage());
            return false;
        }
    }
    @Override
    public boolean updateBook(Book book) {
        ensureConnection();
        String sql = "UPDATE tblBook SET bTitle = ?, bAuthor = ?, bPublisher = ?, " +
                "bPublishYear = ?, bTotal = ?, bAvailable = ?, " +
                "bLocation = ?, bImagePath = ? WHERE bIsbn = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getPublisher());
            pstmt.setInt(4, book.getPublishYear());
            pstmt.setInt(5, book.getTotalCopies());
            pstmt.setInt(6, book.getAvailableCopies());
            pstmt.setString(7, book.getLocation());
            pstmt.setString(8, book.getImagePath());
            pstmt.setString(9, book.getIsbn());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("更新图书失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setIsbn(rs.getString("bIsbn"));
        book.setTitle(rs.getString("bTitle"));
        book.setAuthor(rs.getString("bAuthor"));
        book.setPublisher(rs.getString("bPublisher"));
        book.setPublishYear(rs.getInt("bPublishYear"));
        book.setTotalCopies(rs.getInt("bTotal"));
        book.setAvailableCopies(rs.getInt("bAvailable"));
        book.setLocation(rs.getString("bLocation"));
        book.setImagePath(rs.getString("bImagePath"));
        return book;
    }



    private void setBookParameters(PreparedStatement pstmt, Book book) throws SQLException {

        pstmt.setInt(1,num);
        pstmt.setString(2, book.getIsbn());
        pstmt.setString(3, book.getTitle());
        pstmt.setString(4, book.getAuthor());
        pstmt.setString(5, book.getPublisher());
        pstmt.setInt(6, book.getPublishYear());
        pstmt.setInt(7, book.getTotalCopies());
        pstmt.setInt(8, book.getAvailableCopies());
        pstmt.setString(9, book.getLocation());
        pstmt.setString(10, book.getImagePath());
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("关闭连接失败: " + e.getMessage());
        }
    }
}