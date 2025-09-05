package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSelectionRule;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;
import com.seu.vcampus.server.dao.AdminDao;
import com.seu.vcampus.server.dao.AdminDaoImpl;

public class AdminServiceImpl implements AdminService {
    private AdminDao adminDao = new AdminDaoImpl();

    public AdminServiceImpl() {
        this.adminDao = new AdminDaoImpl();
    }

    public AdminServiceImpl(AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    @Override
    public Message addCourse(Course course) {
        try {
            adminDao.addCourse(course);
            Message response = new Message(Message.ADD_COURSE);
            response.setStatus(ResponseCode.OK);
            response.setDescription("课程添加成功");
            return response;
        } catch (Exception e) {
            Message response = new Message(Message.ADD_COURSE);
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
            response.setDescription("课程添加失败");
            return response;
        }
    }

    @Override
    public Message updateCourse(Course course) {
        try {
            adminDao.updateCourse(course);
            Message response = new Message(Message.UPDATE_COURSE);
            response.setStatus(ResponseCode.OK);
            response.setDescription("课程更新成功");
            return response;
        } catch (Exception e) {
            Message response = new Message(Message.UPDATE_COURSE);
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
            response.setDescription("课程更新失败");
            return response;
        }
    }

    @Override
    public Message deleteCourse(String courseId) {
        try {
            adminDao.deleteCourse(courseId);
            Message response = new Message(Message.DELETE_COURSE);
            response.setStatus(ResponseCode.OK);
            response.setDescription("课程删除成功");
            return response;
        } catch (Exception e) {
            Message response = new Message(Message.DELETE_COURSE);
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
            response.setDescription("课程删除失败");
            return response;
        }
    }

    @Override
    public Message configureRule(CourseSelectionRule rule) {
        try {
            adminDao.configureRule(rule);
            Message response = new Message(Message.CONFIGURE_RULE);
            response.setStatus(ResponseCode.OK);
            response.setDescription("规则配置成功");
            return response;
        } catch (Exception e) {
            Message response = new Message(Message.CONFIGURE_RULE);
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
            response.setDescription("规则配置失败");
            return response;
        }
    }

    @Override
    public Message getRule() {
        try {
            CourseSelectionRule rule = adminDao.getRule();
            Message response = new Message(Message.GET_RULE);
            response.setStatus(ResponseCode.OK);
            response.addData("rule", rule);
            return response;
        } catch (Exception e) {
            Message response = new Message(Message.GET_RULE);
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
            response.setDescription("获取规则失败");
            return response;
        }
    }

    @Override
    public Message generateReport() {
        try {
            // 生成报表逻辑
            Message response = new Message(Message.GENERATE_REPORT);
            response.setStatus(ResponseCode.OK);
            response.setDescription("报表生成成功");
            return response;
        } catch (Exception e) {
            Message response = new Message(Message.GENERATE_REPORT);
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
            response.setDescription("报表生成失败");
            return response;
        }
    }
}