package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.server.dao.IBookDao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements IBookDao {
    private final String DB_URL = "jdbc:ucanaccess://D:\\idea_project\\vCampus\\database\\vCampus.accdb;skipIndexes=true";
    private Connection connection;

    public BookDaoImpl() {
        connect();
    }

    private void connect() {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        }
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
        String sql = "INSERT INTO tblBook (bIsbn, bTitle, bAuthor, bPublisher, " +
                "bPublishYear, bTotal, bAvailable, bLocation, bImagePath) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
            pstmt.setString(1, isbn);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("删除图书失败: " + e.getMessage());
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
        pstmt.setString(1, book.getIsbn());
        pstmt.setString(2, book.getTitle());
        pstmt.setString(3, book.getAuthor());
        pstmt.setString(4, book.getPublisher());
        pstmt.setInt(5, book.getPublishYear());
        pstmt.setInt(6, book.getTotalCopies());
        pstmt.setInt(7, book.getAvailableCopies());
        pstmt.setString(8, book.getLocation());
        pstmt.setString(9, book.getImagePath());
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