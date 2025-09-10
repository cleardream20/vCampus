//package com.seu.vcampus.server.controller;
//import com.seu.vcampus.server.service.ILibraryService;
//import com.seu.vcampus.server.service.LibraryServiceImpl;
//import com.seu.vcampus.common.util.Message;
//import com.seu.vcampus.common.util.LibraryMessage;
//import com.seu.vcampus.common.model.Book;
//
//import java.util.List;
//
//public class LibraryController {
//    private ILibraryService libraryService = new LibraryServiceImpl();
//
//    public Message handleRequest(Message request) {
//        Message response = new Message();
//        response.setType(request.getType());
//
//        try {
//            if (LibraryMessage.GET_ALL_BOOKS.equals(request.getType())) {
//                List<Book> books = libraryService.getAllBooks();
//                response.setStatus(Message.STATUS_SUCCESS);
//                response.setData(books);
//            } else if (LibraryMessage.SEARCH_BOOKS.equals(request.getType())) {
//                String keyword = (String) request.getData();
//                List<Book> books = libraryService.searchBooks(keyword);
//                response.setStatus(Message.STATUS_SUCCESS);
//                response.setData(books);
//            } else {
//                response.setStatus(Message.STATUS_ERROR);
//                response.setData("未知的请求类型: " + request.getType());
//            }
//        } catch (Exception e) {
//            response.setStatus(Message.STATUS_ERROR);
//            response.setData("处理请求时发生错误: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return response;
//    }
//}