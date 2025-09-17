package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Message;

import java.sql.SQLException;

public interface RequestController {
    Message handleRequest(Message request) throws SQLException;
}
