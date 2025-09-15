package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.common.model.Reservation;
import com.seu.vcampus.common.util.DBConnector;
import com.seu.vcampus.server.dao.IBookDao;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BookDaoImpl implements IBookDao {
//    private final String DB_URL = "jdbc:ucanaccess://D:\\idea_project\\database\\vCampus_Library.accdb;";
    private Connection connection;

    public BookDaoImpl() {
        ensureConnection();
        outputAllBooks();
    }

    private void ensureConnection() {
        try {
            connection = DBConnector.getConnection();
            if (connection == null || connection.isClosed()) {
                System.out.println("数据库连接已关闭，尝试重新连接...");
                connection = DBConnector.getConnection();
            }
        } catch (SQLException e) {
            System.err.println("检查连接状态失败: " + e.getMessage());
        }
    }

//    private void connect() {
//        try {
//            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
//            connection = DriverManager.getConnection(DB_URL);
//        } catch (Exception e) {
//            System.err.println("数据库连接失败: " + e.getMessage());
//        }
//    }

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

    @Override
    public List<Book> getAllBooks() {
        ensureConnection();
        List<Book> books = new ArrayList<>();
        String sql = "SELECT bIsbn, bTitle, bAuthor, bPublisher, bPublishYear, " +
                "bTotal, bAvailable, bLocation, bImagePath,bDescription " +
                "FROM tblBook";

        try (Statement stmt = DBConnector.getConnection().createStatement();
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

        // 获取当前日期

        java.util.Date currentDate = new java.util.Date();
        // SQL查询语句
        String sql = "SELECT ID, bookIsbn, bookTitle, " +
                "borrowDate, dueDate, returnDate, " +
                "status, renewalCount, fineAmount " +
                "FROM tblBorrowRecord " +
                "WHERE userId = ? AND status != 'RETURNED'";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                // 用于存储需要更新的记录
                List<BorrowRecord> recordsToUpdate = new ArrayList<>();

                while (rs.next()) {
                    BorrowRecord record = new BorrowRecord();

                    // 设置记录ID
                    record.setRecordId(rs.getLong("ID"));

                    // 设置图书信息
                    record.setBookIsbn(rs.getString("bookIsbn"));
                    record.setBookTitle(rs.getString("bookTitle"));

                    // 设置日期信息 - 使用 java.util.Date
                    Timestamp borrowTimestamp = rs.getTimestamp("borrowDate");
                    if (borrowTimestamp != null) {
                        record.setBorrowDate(new Date(borrowTimestamp.getTime()));
                    }

                    Timestamp dueTimestamp = rs.getTimestamp("dueDate");
                    if (dueTimestamp != null) {
                        record.setDueDate(new Date(dueTimestamp.getTime()));
                    }

                    Timestamp returnTimestamp = rs.getTimestamp("returnDate");
                    if (returnTimestamp != null) {
                        record.setReturnDate(new Date(returnTimestamp.getTime()));
                    }

                    // 设置状态和借阅信息
                    String dbStatus = rs.getString("status");
                    record.setRenewalCount(rs.getInt("renewalCount"));
                    double dbFineAmount = rs.getDouble("fineAmount");
                    record.setFineAmount(dbFineAmount);

                    // 设置用户ID
                    record.setUserId(userId);

                    // 保存数据库原始状态
                    String originalStatus = dbStatus;

                    // ======== 判断是否逾期并计算费用 ========
                    if (record.getDueDate() != null &&
                            ("BORROWED".equalsIgnoreCase(dbStatus) || "OVERDUE".equalsIgnoreCase(dbStatus))) {

                        // 判断是否逾期
                        if (currentDate.after(record.getDueDate())) {
                            // 计算逾期天数
                            long diff = currentDate.getTime() - record.getDueDate().getTime();
                            long daysOverdue = TimeUnit.MILLISECONDS.toDays(diff);

                            // 计算逾期费用（每天0.5元）
                            double fineAmount = daysOverdue * 0.5;

                            // 更新记录状态和费用
                            record.setStatus("OVERDUE");
                            record.setFineAmount(fineAmount);

                            // 打印逾期信息
                            System.out.printf("记录ID %d 逾期: %d天, 费用: %.2f元%n",
                                    record.getRecordId(), daysOverdue, fineAmount);

                            // 检查是否需要更新数据库
                            if (!"OVERDUE".equalsIgnoreCase(originalStatus) ||
                                    Math.abs(fineAmount - dbFineAmount) > 0.01) {
                                recordsToUpdate.add(record);
                            }
                        } else {
                            // 未逾期但状态可能是OVERDUE，需要修正为BORROWED
                            if ("OVERDUE".equalsIgnoreCase(dbStatus)) {
                                record.setStatus("BORROWED");
                                // 清除逾期费用
                                record.setFineAmount(0.0);

                                // 标记需要更新数据库
                                recordsToUpdate.add(record);
                            } else {
                                record.setStatus(dbStatus);
                            }
                        }
                    } else {
                        // 已归还或无效状态
                        record.setStatus(dbStatus);
                    }

                    // 添加到结果列表
                    records.add(record);
                }

                // 批量更新需要修改的记录
                if (!recordsToUpdate.isEmpty()) {
                    updateBorrowRecords(recordsToUpdate);
                }
            }
        } catch (SQLException e) {
            System.err.println("查询借阅记录失败: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }

    // 更新借阅记录状态和逾期费用
    private void updateBorrowRecords(List<BorrowRecord> recordsToUpdate) {
        // 开始事务
        boolean originalAutoCommit = false;
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // 准备SQL语句
            String sql = "UPDATE tblBorrowRecord SET status = ?, fineAmount = ? WHERE ID = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (BorrowRecord record : recordsToUpdate) {
                    pstmt.setString(1, record.getStatus());
                    pstmt.setDouble(2, record.getFineAmount());
                    pstmt.setLong(3, record.getRecordId());
                    pstmt.addBatch();
                }

                // 执行批量更新
                int[] updateCounts = pstmt.executeBatch();
                int updatedRecords = 0;
                for (int count : updateCounts) {
                    if (count > 0) updatedRecords++;
                }

                System.out.printf("成功更新 %d/%d 条借阅记录状态%n", updatedRecords, recordsToUpdate.size());

                // 提交事务
                connection.commit();
            }
        } catch (SQLException e) {
            try {
                // 回滚事务
                if (connection != null) {
                    connection.rollback();
                }
                System.err.println("更新借阅记录状态失败: " + e.getMessage());
            } catch (SQLException ex) {
                System.err.println("回滚事务失败: " + ex.getMessage());
            }
        } finally {
            try {
                // 恢复自动提交状态
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                System.err.println("恢复自动提交状态失败: " + e.getMessage());
            }
        }
    }
    private BorrowRecord getBorrowRecordById(Long recordId) throws SQLException {
        String sql = "SELECT ID, userId, bookIsbn, bookTitle, " +
                "borrowDate, dueDate, returnDate, status, " +
                "renewalCount, fineAmount " +
                "FROM tblBorrowRecord WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, recordId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BorrowRecord record = new BorrowRecord();

                    // 设置记录ID
                    record.setRecordId(rs.getLong("ID"));

                    // 设置用户信息
                    record.setUserId(rs.getString("userId"));

                    // 设置图书信息
                    record.setBookIsbn(rs.getString("bookIsbn"));
                    record.setBookTitle(rs.getString("bookTitle"));

                    // 设置日期信息
                    Timestamp borrowTimestamp = rs.getTimestamp("borrowDate");
                    if (borrowTimestamp != null) {
                        record.setBorrowDate(new Date(borrowTimestamp.getTime()));
                    }

                    Timestamp dueTimestamp = rs.getTimestamp("dueDate");
                    if (dueTimestamp != null) {
                        record.setDueDate(new Date(dueTimestamp.getTime()));
                    }

                    Timestamp returnTimestamp = rs.getTimestamp("returnDate");
                    if (returnTimestamp != null) {
                        record.setReturnDate(new Date(returnTimestamp.getTime()));
                    }

                    // 设置状态和借阅信息
                    record.setStatus(rs.getString("status"));
                    record.setRenewalCount(rs.getInt("renewalCount"));
                    record.setFineAmount(rs.getDouble("fineAmount"));

                    return record;
                }
            }
        }
        return null;
    }

    @Override
    public List<Reservation> getReservationsByUserId(String userId) {
        ensureConnection();
        List<Reservation> reservations = new ArrayList<>();

        // 获取当前日期
        java.util.Date currentDate = new java.util.Date();

        // SQL查询语句
        String sql = "SELECT ID,bookIsbn,bookTitle,reservationDate, status " +
                "FROM tblReservation " +
                "WHERE userId = ? AND status != 'CANCELLED'";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                // 存储需要更新的记录
                List<Reservation> recordsToUpdate = new ArrayList<>();

                while (rs.next()) {
                    Reservation reservation = new Reservation();

                    // 设置预约ID
                    reservation.setReserveId(rs.getLong("ID"));
                    reservation.setUserId(userId);

                    reservation.setBookIsbn(rs.getString("bookIsbn"));
                    reservation.setBookTitle(rs.getString("bookTitle"));
                    Timestamp reserveTimestamp = rs.getTimestamp("reservationDate");
                    if (reserveTimestamp  != null) {
                        reservation.setReserveDate(new Date(reserveTimestamp .getTime()));
                    }

                    // 设置状态
                    String dbStatus = rs.getString("status");
                    reservation.setStatus(dbStatus);

                    // 添加到结果列表
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("查询用户预约记录失败: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }


    @Override
    public Book getBookByISBN(String isbn) {
        String sql = "SELECT bIsbn, bTitle, bAuthor, bPublisher, bPublishYear, " +
                "bTotal, bAvailable, bLocation, bImagePath,bDescription " +
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
                "bPublishYear, bTotal, bAvailable, bLocation, bImagePath,bDescription) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
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
                "bLocation = ?, bImagePath = ?, bDescription = ?  WHERE bIsbn = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getPublisher());
            pstmt.setInt(4, book.getPublishYear());
            pstmt.setInt(5, book.getTotalCopies());
            pstmt.setInt(6, book.getAvailableCopies());
            pstmt.setString(7, book.getLocation());
            pstmt.setString(8, book.getImagePath());
            pstmt.setString(9, book.getDescription());
            pstmt.setString(10, book.getIsbn());


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
        ensureConnection();
        boolean success = false;
        boolean originalAutoCommit = false;
        boolean isReservedUser = false; // 标记是否为预约用户

        try {
            // 保存原始自动提交状态
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // 1. 获取图书信息
            Book book = getBookByISBN(isbn);
            if (book == null) {
                throw new SQLException("图书不存在");
            }

            // 2. 检查是否有有效预约
            Reservation activeReservation = getActiveReservationForBook(isbn);

            // 3. 检查用户是否是预约用户
            if (activeReservation != null && userId.equals(activeReservation.getUserId())) {
                isReservedUser = true;
                System.out.println("用户 " + userId + " 是预约用户，借阅图书 " + isbn);
            }

            // 4. 对于非预约用户，检查图书是否可借
            if (!isReservedUser) {
                if (book.getAvailableCopies() <= 0) {
                    throw new SQLException("图书不可借，库存不足");
                }
            }

            // 5. 如果有有效预约，检查用户是否有权限借阅
            if (activeReservation != null) {
                // 只有预约用户才能借阅
                if (!userId.equals(activeReservation.getUserId())) {
                    throw new SQLException("该图书已被预约，只有预约用户才能借阅");
                }

                // 更新预约状态为已借阅
                updateReservationStatus(activeReservation.getReserveId(), "CANCELLED");
            }

            // 6. 减少图书可借数量（仅当不是预约用户时）
            if (!isReservedUser) {
                String updateBookSql = "UPDATE tblBook SET bAvailable = bAvailable - 1 " +
                        "WHERE bIsbn = ? AND bAvailable > 0";

                try (PreparedStatement updateStmt = connection.prepareStatement(updateBookSql)) {
                    updateStmt.setString(1, isbn);
                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated == 0) {
                        throw new SQLException("更新图书库存失败");
                    }
                }
            }

            // 7. 创建借阅记录
            String insertRecordSql = "INSERT INTO tblBorrowRecord " +
                    "(userId, bookIsbn, bookTitle, borrowDate, dueDate, status) " +
                    "VALUES (?, ?, ?, ?, ?, 'BORROWED')";

            java.util.Date borrowDate = new java.util.Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(borrowDate);
            calendar.add(Calendar.DAY_OF_MONTH, 30); // 30天后应还
            java.util.Date dueDate = calendar.getTime();

            try (PreparedStatement insertStmt = connection.prepareStatement(insertRecordSql)) {
                insertStmt.setString(1, userId);
                insertStmt.setString(2, isbn);
                insertStmt.setString(3, book.getTitle());
                insertStmt.setTimestamp(4, new Timestamp(borrowDate.getTime()));
                insertStmt.setTimestamp(5, new Timestamp(dueDate.getTime()));

                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("创建借阅记录失败");
                }
            }

            // 8. 激活下一个预约（如果有）
            if (activeReservation != null) {
                activateNextReservation(isbn);
            }

            // 提交事务
            connection.commit();
            success = true;
            System.out.println("借书成功: 用户 " + userId + ", ISBN " + isbn);

        } catch (SQLException e) {
            try {
                connection.rollback();
                System.err.println("借书失败: " + e.getMessage());
            } catch (SQLException ex) {
                System.err.println("回滚失败: " + ex.getMessage());
            }
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                System.err.println("恢复自动提交失败: " + e.getMessage());
            }
        }

        return success;
    }

    @Override
    public boolean canUserBorrow(String userId) {
        ensureConnection();
        try {
            // 检查借阅数量是否超过限制（假设最多借30本）
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
        ensureConnection();
        boolean success = false;
        boolean originalAutoCommit = false;

        try {
            // 保存原始自动提交状态
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // 1. 获取借阅记录信息
            BorrowRecord record = getBorrowRecordById(recordId);
            if (record == null) {
                System.out.println("还书失败：借阅记录不存在 - ID: " + recordId);
                return false;
            }

            // 2. 检查记录状态是否可归还
            if (!"BORROWED".equals(record.getStatus()) && !"OVERDUE".equals(record.getStatus())) {
                System.out.println("还书失败：记录状态不可归还 - ID: " + recordId + ", 状态: " + record.getStatus());
                return false;
            }

            // 3. 更新借阅记录状态
            String updateRecordSql = "UPDATE tblBorrowRecord SET status = 'RETURNED', returnDate = ? " +
                    "WHERE ID = ? AND status IN ('BORROWED', 'OVERDUE')";

            java.util.Date returnDate = new java.util.Date();
            try (PreparedStatement updateStmt = connection.prepareStatement(updateRecordSql)) {
                updateStmt.setTimestamp(1, new Timestamp(returnDate.getTime()));
                updateStmt.setLong(2, recordId);
                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated == 0) {
                    System.out.println("还书失败：更新借阅记录失败 - ID: " + recordId);
                    return false;
                }
            }

            // 4. 检查是否有有效预约
            boolean hasActiveReservation = bookhasActiveReservation(record.getBookIsbn());

            // 5. 如果没有有效预约，则增加图书可借数量
            if (!hasActiveReservation) {
                String updateBookSql = "UPDATE tblBook SET bAvailable = bAvailable + 1 " +
                        "WHERE bIsbn = ? AND bAvailable < bTotal";

                try (PreparedStatement updateStmt = connection.prepareStatement(updateBookSql)) {
                    updateStmt.setString(1, record.getBookIsbn());
                    int rowsUpdated = updateStmt.executeUpdate();

                    if (rowsUpdated == 0) {
                        throw new SQLException("更新图书库存失败");
                    }
                }
            } else {
                System.out.println("图书 " + record.getBookIsbn() + " 有有效预约，不增加可借数量");
            }

            // 6. 计算逾期费用
            if (returnDate.after(record.getDueDate())) {
                long diff = returnDate.getTime() - record.getDueDate().getTime();
                long daysOverdue = TimeUnit.MILLISECONDS.toDays(diff);
                double fineAmount = daysOverdue * 0.5; // 每天0.5元逾期费

                String updateFineSql = "UPDATE tblBorrowRecord SET fineAmount = ? WHERE ID = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateFineSql)) {
                    updateStmt.setDouble(1, fineAmount);
                    updateStmt.setLong(2, recordId);
                    updateStmt.executeUpdate();
                }

                System.out.println("计算逾期费用: " + daysOverdue + "天，费用: " + fineAmount + "元");
            }

            // 7. 激活下一个预约（如果有）
            activateNextReservation(record.getBookIsbn());

            // 提交事务
            connection.commit();
            success = true;
            System.out.println("还书成功: 记录ID " + recordId + ", ISBN " + record.getBookIsbn());

        } catch (SQLException e) {
            try {
                // 回滚事务
                if (connection != null) {
                    connection.rollback();
                }
                System.err.println("还书失败: " + e.getMessage());
            } catch (SQLException ex) {
                System.err.println("回滚事务失败: " + ex.getMessage());
            }
        } finally {
            try {
                // 恢复原始自动提交状态
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                System.err.println("恢复自动提交状态失败: " + e.getMessage());
            }
        }

        return success;
    }

    // 最大续借次数
    private static final int MAX_RENEWAL_COUNT = 3;

    @Override
    public boolean renewBook(long recordId) {
        ensureConnection();
        boolean success = false;
        boolean originalAutoCommit = false;

        try {
            // 保存原始自动提交状态
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // 1. 获取借阅记录
            BorrowRecord record = getBorrowRecordById(recordId);
            if (record == null) {
                System.out.println("续借失败：借阅记录不存在 - ID: " + recordId);
                return false;
            }

            // 2. 检查是否可以续借
            if (!"BORROWED".equals(record.getStatus()) && !"OVERDUE".equals(record.getStatus())) {
                System.out.println("续借失败：只有借阅中或逾期的图书可以续借 - ID: " + recordId);
                return false;
            }

            if (record.getRenewalCount() >= MAX_RENEWAL_COUNT) {
                System.out.println("续借失败：已达到最大续借次数 - ID: " + recordId);
                return false;
            }

            // 3. 检查图书是否被预约
            if (bookhasActiveReservation(record.getBookIsbn())) {
                System.out.println("续借失败：该图书已被预约 - ISBN: " + record.getBookIsbn());
                return false;
            }

            // 4. 计算新的应还日期（在当前应还日期上加10天）
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(record.getDueDate());
            calendar.add(Calendar.DAY_OF_MONTH, 10);
            java.util.Date newDueDate = calendar.getTime();

            // 5. 更新借阅记录
            String updateSql = "UPDATE tblBorrowRecord SET dueDate = ?, renewalCount = renewalCount + 1 " +
                    "WHERE ID = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setTimestamp(1, new Timestamp(newDueDate.getTime()));
                pstmt.setLong(2, recordId);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated == 0) {
                    System.out.println("续借失败：更新借阅记录失败 - ID: " + recordId);
                    return false;
                }
            }

            // 提交事务
            connection.commit();
            success = true;
            System.out.println("续借成功: ID " + recordId + ", 新应还日期: " + newDueDate);

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("回滚事务失败: " + ex.getMessage());
            }
            System.err.println("续借失败: " + e.getMessage());
        } finally {
            try {
                // 恢复原始自动提交状态
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                System.err.println("恢复自动提交状态失败: " + e.getMessage());
            }
        }

        return success;
    }

    @Override
    public boolean cancelReservation(Long reservationId) {
        ensureConnection();
        boolean success = false;
        boolean originalAutoCommit = false;
        String bookIsbn = null;
        boolean wasActive = false;

        try {
            // 保存原始自动提交状态
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // 1. 获取预约记录信息
            Reservation reservation = getReservationById(reservationId);
            if (reservation == null) {
                System.out.println("取消预约失败：预约记录不存在 - ID: " + reservationId);
                return false;
            }

            bookIsbn = reservation.getBookIsbn();
            wasActive = "ACTIVE".equals(reservation.getStatus());

            // 2. 检查记录状态是否可取消
            if (!reservation.getStatus().equals("PENDING") && !reservation.getStatus().equals("ACTIVE")) {
                System.out.println("取消预约失败：记录状态不可取消 - ID: " + reservationId +
                        ", 状态: " + reservation.getStatus());
                return false;
            }

            // 3. 更新预约记录状态
            String updateSql = "UPDATE tblReservation SET status = 'CANCELLED' " +
                    "WHERE ID = ? AND status IN ('PENDING', 'ACTIVE')";

            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setLong(1, reservationId);
                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated == 0) {
                    System.out.println("取消预约失败：更新预约记录失败 - ID: " + reservationId);
                    return false;
                }
            }

            // 4. 如果是激活预约，检查是否还有其他激活预约
            if (wasActive) {
                // 检查该书是否还有其他激活预约
                boolean hasOtherActiveReservations = bookhasActiveReservation(bookIsbn);

                // 如果没有其他激活预约，增加图书可借数量
                if (!hasOtherActiveReservations) {
                    String updateBookSql = "UPDATE tblBook SET bAvailable = bAvailable + 1 " +
                            "WHERE bIsbn = ?";

                    try (PreparedStatement updateStmt = connection.prepareStatement(updateBookSql)) {
                        updateStmt.setString(1, bookIsbn);
                        int rowsUpdated = updateStmt.executeUpdate();

                        if (rowsUpdated == 0) {
                            throw new SQLException("更新图书库存失败");
                        }
                    }

                    System.out.println("增加图书可借数量: ISBN " + bookIsbn);
                } else {
                    System.out.println("该书还有其他激活预约，不增加可借数量: ISBN " + bookIsbn);
                }
            }

            // 5. 激活下一个预约（如果有）
            if (wasActive) {
                activateNextReservation(bookIsbn);
            }

            // 提交事务
            connection.commit();
            success = true;
            System.out.println("取消预约成功: ID " + reservationId + ", ISBN " + bookIsbn);

        } catch (SQLException e) {
            try {
                // 回滚事务
                if (connection != null) {
                    connection.rollback();
                }
                System.err.println("取消预约失败: " + e.getMessage());
            } catch (SQLException ex) {
                System.err.println("回滚事务失败: " + ex.getMessage());
            }
        } finally {
            try {
                // 恢复原始自动提交状态
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                System.err.println("恢复自动提交状态失败: " + e.getMessage());
            }
        }

        return success;
    }

    // 获取预约记录
    private Reservation getReservationById(Long reservationId) throws SQLException {
        String sql = "SELECT ID, userId, bookIsbn, bookTitle, reservationDate, status " +
                "FROM tblReservation WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, reservationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setReserveId(rs.getLong("ID"));
                    reservation.setUserId(rs.getString("userId"));
                    reservation.setBookIsbn(rs.getString("bookIsbn"));
                    reservation.setBookTitle(rs.getString("bookTitle"));

                    Timestamp reserveTimestamp = rs.getTimestamp("reservationDate");
                    if (reserveTimestamp != null) {
                        reservation.setReserveDate(new Date(reserveTimestamp.getTime()));
                    }

                    reservation.setStatus(rs.getString("status"));
                    return reservation;
                }
            }
        }
        return null;
    }

    @Override
    public boolean reserveBook(String userId, String isbn) {
        ensureConnection();

        try {
            // 1. 检查图书是否存在
            Book book = getBookByISBN(isbn);
            if (book == null) {
                System.out.println("预约失败：图书不存在 - ISBN: " + isbn);
                return false;
            }

            // 2. 检查用户是否已有该书的有效预约
            if (hasActiveReservation(userId, isbn)) {
                System.out.println("预约失败：用户已有该书的预约 - 用户ID: " + userId + ", ISBN: " + isbn);
                return false;
            }

            // 3. 创建预约记录
            String sql = "INSERT INTO tblReservation (userId, bookIsbn, bookTitle, reservationDate, status) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                pstmt.setString(2, isbn);
                pstmt.setString(3, book.getTitle());
                pstmt.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(5, "PENDING"); // 初始状态为PENDING

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("预约成功: 用户ID " + userId + ", ISBN " + isbn);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("预约图书失败: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // 检查用户是否有该书的有效预约
    private boolean hasActiveReservation(String userId, String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM tblReservation " +
                "WHERE userId = ? AND bookIsbn = ? " +
                "AND status IN ('PENDING', 'ACTIVE')";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, isbn);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    // 检查图书是否有有效预约
    private boolean bookhasActiveReservation(String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM tblReservation " +
                "WHERE bookIsbn = ? AND status IN ('PENDING', 'ACTIVE')";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    // 获取图书的有效预约记录（按预约时间排序）
    private Reservation getActiveReservationForBook(String isbn) throws SQLException {
        String sql = "SELECT ID, userId, reservationDate " +
                "FROM tblReservation " +
                "WHERE bookIsbn = ? AND status = 'ACTIVE' " +
                "ORDER BY reservationDate ASC " + // 按预约时间排序
                "LIMIT 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setReserveId(rs.getLong("ID"));
                    reservation.setUserId(rs.getString("userId"));

                    Timestamp reserveTimestamp = rs.getTimestamp("reservationDate");
                    if (reserveTimestamp != null) {
                        reservation.setReserveDate(new Date(reserveTimestamp.getTime()));
                    }

                    return reservation;
                }
            }
        }
        return null;
    }

    // 更新预约状态
    private void updateReservationStatus(long reservationId, String status) throws SQLException {
        String sql = "UPDATE tblReservation SET status = ? WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setLong(2, reservationId);
            pstmt.executeUpdate();
        }
    }
    // 激活下一个预约
    private void activateNextReservation(String isbn) throws SQLException {
        // 查找对该书有PENDING预约的用户
        String sql = "SELECT ID, userId FROM tblReservation " +
                "WHERE bookIsbn = ? AND status = 'PENDING' " +
                "ORDER BY reservationDate ASC " + // 按预约时间排序
                "LIMIT 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long reserveId = rs.getLong("ID");
                    String userId = rs.getString("userId");

                    // 更新预约状态为ACTIVE
                    updateReservationStatus(reserveId, "ACTIVE");

                }
            }
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
        book.setDescription(rs.getString("bDescription"));
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
        pstmt.setString(10, book.getDescription());
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