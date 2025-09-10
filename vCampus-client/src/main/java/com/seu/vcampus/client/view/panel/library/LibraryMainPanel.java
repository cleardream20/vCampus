package com.seu.vcampus.client.view.panel.library;

import javax.swing.*;
import java.awt.*;

public class LibraryMainPanel extends JPanel {

    private final String userRole; // "user" 或 "admin"

    public LibraryMainPanel(String userId, String userRole) {
        this.userRole = userRole;
        initializeUI(userId);
    }

    private void initializeUI(String userId) {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // 添加普通功能 Tab
        tabbedPane.addTab("图书查询", new BookSearchPanel(userId));
        tabbedPane.addTab("我的借阅", new MyLibraryPanel(userId));

        // 根据角色决定是否显示管理员功能
        if ("admin".equals(userRole)) {
            tabbedPane.addTab("图书管理", new AdminPanel());
        }

        add(tabbedPane, BorderLayout.CENTER);
    }
}