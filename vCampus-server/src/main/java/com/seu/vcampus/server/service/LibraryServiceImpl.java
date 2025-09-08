package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.server.dao.BookDaoImpl;
import com.seu.vcampus.server.dao.IBookDao;

import java.util.*;
import java.util.stream.Collectors;

public class LibraryServiceImpl implements ILibraryService {
    // 模拟的图书数据
    private List<Book> books = new ArrayList<>();
    private List<BorrowRecord> borrowRecords =new ArrayList<>();

    private BookDaoImpl bookDao= new BookDaoImpl();
    // 记录ID计数器
    private long recordIdCounter = 1;

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
        addMockBooks();
        addMockBorrowRecords();
    }
    private void addMockBooks(){
        books=bookDao.getAllBooks();
    }

    private void addMockBorrowRecords() {
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        // 学生用户20210001的借书记录
        addBorrowRecord("20210001", "9787111636665", "Java核心技术",
                addDays(now, -15), // 15天前借出
                addDays(now, 15),  // 15天后到期
                null, "BORROWED", 0.0, 0);

        addBorrowRecord("20210001", "9787115537977", "深入理解计算机系统",
                addDays(now, -5),  // 5天前借出
                addDays(now, 25),  // 25天后到期
                null, "BORROWED", 0.0, 0);
        // 逾期未还的记录
        addBorrowRecord("20210001", "9787121382061", "算法导论",
                addDays(now, -35), // 35天前借出
                addDays(now, -5),  // 5天前到期
                null, "OVERDUE", 15.0, 1); // 逾期5天，费用15元
        // 已续借2次的记录
        addBorrowRecord("20210001", "9787115480655", "数据库系统概念",
                addDays(now, -40), // 40天前借出
                addDays(now, 10),   // 10天后到期
                null, "BORROWED", 0.0, 2); // 已续借2次
    }

    private void addBorrowRecord(String userId, String bookIsbn, String bookTitle,
                                 Date borrowDate, Date dueDate, Date returnDate,
                                 String status, Double fineAmount, Integer renewalCount) {
        BorrowRecord record = new BorrowRecord();
        record.setRecordId(recordIdCounter++);
        record.setUserId(userId);
        record.setBookIsbn(bookIsbn);
        record.setBookTitle(bookTitle);
        record.setBorrowDate(borrowDate);
        record.setDueDate(dueDate);
        record.setReturnDate(returnDate);
        record.setStatus(status);
        record.setFineAmount(fineAmount);
        record.setRenewalCount(renewalCount);

        borrowRecords.add(record);
    }

    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
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
        return borrowRecords.stream()
                .filter(record -> record.getUserId().equals(userId))
                .filter(record -> "BORROWED".equals(record.getStatus()) || "OVERDUE".equals(record.getStatus()))
                .collect(Collectors.toList());
    }

    // 借阅图书
    @Override
    public boolean borrowBook(String userId, String isbn) {
        // 查找图书
        Optional<Book> bookOpt = books.stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst();

        if (!bookOpt.isPresent()) {
            return false; // 图书不存在
        }

        Book book = bookOpt.get();

        // 检查是否有可借数量
        if (book.getAvailableCopies() <= 0) {
            return false; // 无可借副本
        }

        // 更新图书可借数量
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        // 计算借阅日期和应还日期
        Date borrowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(borrowDate);
        calendar.add(Calendar.DAY_OF_MONTH, 30); // 借期30天
        Date dueDate = calendar.getTime();

        // 创建借书记录
        addBorrowRecord(userId, isbn, book.getTitle(),
                borrowDate, dueDate, null, "BORROWED", 0.0, 0);

        return true;
    }

    // 归还图书
    @Override
    public boolean returnBook(Long recordId) {
        // 查找借书记录
        Optional<BorrowRecord> recordOpt = borrowRecords.stream()
                .filter(r -> r.getRecordId().equals(recordId) &&
                        ("BORROWED".equals(r.getStatus()) || "OVERDUE".equals(r.getStatus())))
                .findFirst();

        if (!recordOpt.isPresent()) {
            System.out.println("未找到可归还的记录: " + recordId);
            return false; // 未找到借书记录或状态无效
        }

        BorrowRecord record = recordOpt.get();
        System.out.println("处理归还记录: " + record.getBookTitle() + ", ISBN: " + record.getBookIsbn());

        // 更新记录状态
        record.setStatus("RETURNED");
        record.setReturnDate(new Date());

        // 检查是否逾期
        if (record.getReturnDate().after(record.getDueDate())) {
            // 计算逾期天数
            long diff = record.getReturnDate().getTime() - record.getDueDate().getTime();
            long daysOverdue = diff / (1000 * 60 * 60 * 24);

            // 计算逾期费用（假设每天0.5元）
            double fine = daysOverdue * 0.5;
            record.setFineAmount(fine);
            System.out.println("逾期天数: " + daysOverdue + ", 费用: " + fine);
        }

        // 查找图书
        Optional<Book> bookOpt = books.stream()
                .filter(b -> b.getIsbn().equals(record.getBookIsbn()))
                .findFirst();

        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            System.out.println("归还前可借数量: " + book.getAvailableCopies());
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            System.out.println("归还后可借数量: " + book.getAvailableCopies());
        } else {
            System.out.println("未找到ISBN为 " + record.getBookIsbn() + " 的图书");
            return false;
        }

        return true;
    }

    // 续借图书
    @Override
    public boolean renewBook(Long recordId) {
        // 查找借书记录
        Optional<BorrowRecord> recordOpt = borrowRecords.stream()
                .filter(r -> r.getRecordId().equals(recordId) &&
                        ("BORROWED".equals(r.getStatus()) || "OVERDUE".equals(r.getStatus())))
                .findFirst();

        if (!recordOpt.isPresent()) {
            return false; // 未找到借书记录
        }

        BorrowRecord record = recordOpt.get();

        // 检查续借次数
        if (record.getRenewalCount() >= 2) {
            return false; // 最多续借2次
        }

        // 更新应还日期（续借30天）
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(record.getDueDate());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        record.setDueDate(calendar.getTime());

        // 更新续借次数
        record.setRenewalCount(record.getRenewalCount() + 1);

        // 如果是逾期状态，更新为在借状态
        if ("OVERDUE".equals(record.getStatus())) {
            record.setStatus("BORROWED");
        }

        return true;
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



    // 获取图书详情
    @Override
    public Book getBookByIsbn(String isbn) {
        return books.stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }


}
