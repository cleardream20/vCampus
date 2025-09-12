package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.server.dao.IBookDao;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookDaoImpl implements IBookDao {
    private final String DB_URL = "jdbc:ucanaccess://D:\\idea_project\\database\\vCampus_Library.accdb;";
    private Connection connection;

    public BookDaoImpl() {
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
    public List<BorrowRecord> getBorrowRecordsByUserId(String userId) {
        ensureConnection();
        List<BorrowRecord> records = new ArrayList<>();

        // SQL查询语句
        String sql = "SELECT ID, bookIsbn, bookTitle, " +
                "borrowDate, dueDate, returnDate, " +
                "status, renewalCount, fineAmount " +
                "FROM tblBorrowRecord " +
                "WHERE userId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord record = new BorrowRecord();

                    // 设置记录ID
                    record.setRecordId(rs.getLong("ID"));
                    // 设置图书信息
                    record.setBookIsbn(rs.getString("bookIsbn"));
                    record.setBookTitle(rs.getString("bookTitle"));
                    // 设置日期信息
                    record.setBorrowDate(rs.getTimestamp("borrowDate"));
                    record.setDueDate(rs.getTimestamp("dueDate"));
                    // 处理可能的空值（归还日期）
                    Timestamp borrowTimestamp = rs.getTimestamp("borrowDate");
                    if (borrowTimestamp != null) {
                        record.setBorrowDate(new java.util.Date(borrowTimestamp.getTime()));
                    }

                    Timestamp dueTimestamp = rs.getTimestamp("dueDate");
                    if (dueTimestamp != null) {
                        record.setDueDate(new java.util.Date(dueTimestamp.getTime()));
                    }

                    Timestamp returnTimestamp = rs.getTimestamp("returnDate");
                    if (returnTimestamp != null) {
                        record.setReturnDate(new java.util.Date(returnTimestamp.getTime()));
                    }

                    // 设置状态和借阅信息
                    record.setStatus(rs.getString("status"));
                    record.setRenewalCount(rs.getInt("renewalCount"));
                    record.setFineAmount(rs.getDouble("fineAmount"));

                    // 设置用户ID
                    record.setUserId(userId);

                    // 添加到结果列表
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            System.err.println("查询借阅记录失败: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
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
        System.out.println(book.toString());
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


    @Override
    public boolean borrowBook(String userId, String isbn) {
        try {
            // 开始事务
            connection.setAutoCommit(false);

            // 1. 检查用户是否可以借书
            if (!canUserBorrow(userId)) {
                throw new SQLException("用户不符合借书条件");
            }

            // 2. 检查图书是否可借
            String checkBookSql = "SELECT bTitle, bAvailable FROM tblBook WHERE bIsbn = ?";
            String bookTitle = null;

            try (PreparedStatement checkStmt = connection.prepareStatement(checkBookSql)) {
                checkStmt.setString(1, isbn);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getInt("bAvailable") <= 0) {
                            throw new SQLException("图书不可借，库存不足");
                        }
                        bookTitle = rs.getString("bTitle");
                    } else {
                        throw new SQLException("图书不存在");
                    }
                }
            }

            // 3. 减少图书可借数量
            String updateBookSql = "UPDATE tblBook SET bAvailable = bAvailable - 1 " +
                    "WHERE bIsbn = ? AND bAvailable > 0";

            try (PreparedStatement updateStmt = connection.prepareStatement(updateBookSql)) {
                updateStmt.setString(1, isbn);
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("更新图书库存失败");
                }
            }



            // 4. 创建借阅记录
            String insertRecordSql = "INSERT INTO tblBorrowRecord " +
                    "(userId, bookIsbn, bookTitle, borrowDate, dueDate, status) " +
                    "VALUES (?, ?, ?, ?, ?, 'BORROWED')";


            java.util.Date borrowDate = new java.util.Date();
            System.out.println("借书时间: " + borrowDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(borrowDate);
            calendar.add(Calendar.DAY_OF_MONTH, 30); // 30天后应还
            java.util.Date dueDate = calendar.getTime();
            System.out.println("应还时间: " + dueDate);
            System.out.println( userId);
            System.out.println(isbn);
            System.out.println(bookTitle);
            try (PreparedStatement insertStmt = connection.prepareStatement(insertRecordSql)) {
                insertStmt.setString(1, userId);
                insertStmt.setString(2, isbn);
                insertStmt.setString(3, bookTitle);
                insertStmt.setTimestamp(4, new Timestamp(borrowDate.getTime()));
                insertStmt.setTimestamp(5, new Timestamp(dueDate.getTime()));

                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("创建借阅记录失败");
                }
            }

            // 提交事务
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                // 回滚事务
                connection.rollback();
                System.err.println("借书失败: " + e.getMessage());
            } catch (SQLException ex) {
                System.err.println("回滚失败: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("恢复自动提交失败: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean canUserBorrow(String userId) {
        ensureConnection();
        try {
            // 检查借阅数量是否超过限制（假设最多借5本）
            String countSql = "SELECT COUNT(*) AS count FROM tblBorrowRecord " +
                    "WHERE userId = ? AND status IN ('BORROWED', 'OVERDUE')";

            try (PreparedStatement countStmt = connection.prepareStatement(countSql)) {
                countStmt.setString(1, userId);
                try (ResultSet rs = countStmt.executeQuery()) {
                    if (rs.next() && rs.getInt("count") >= 5) {
                        return false; // 借阅数量已达上限
                    }
                }
            }

            // 检查是否有逾期未还的图书
            String overdueSql = "SELECT COUNT(*) AS count FROM tblBorrowRecord " +
                    "WHERE userId = ? AND status = 'OVERDUE'";

            try (PreparedStatement overdueStmt = connection.prepareStatement(overdueSql)) {
                overdueStmt.setString(1, userId);
                try (ResultSet rs = overdueStmt.executeQuery()) {
                    if (rs.next() && rs.getInt("count") > 0) {
                        return false; // 有逾期未还的图书
                    }
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("检查用户借书资格失败: " + e.getMessage());
            return false;
        }
    }
    @Override
    public boolean returnBook(Long recordId) {


        return false;
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