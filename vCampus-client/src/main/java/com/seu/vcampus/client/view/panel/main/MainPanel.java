package com.seu.vcampus.client.view.panel.main;

import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel implements NavigatablePanel {
    private JButton btnUserCenter;
    private JButton btnUserManage;
    private JButton btnStudent;
    private JButton btnCourse;
    private JButton btnLibrary;
    private JButton btnShop;
    private JButton btnDorm;

    public MainPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font titleFont = new Font("微软雅黑", Font.BOLD, 20);
        Font btnFont = new Font("微软雅黑", Font.PLAIN, 16);
        Dimension btnSize = new Dimension(150, 40);

        // 标题
        JLabel lblTitle = new JLabel("vCampus 服务大厅", SwingConstants.CENTER);
        lblTitle.setFont(titleFont);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(240, 245, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.CENTER;
        add(lblTitle, gbc);

        // 用户信息区域：先创建按钮，但先不设用户名
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        btnUserCenter = new JButton("个人中心 (未登录)");
        btnUserCenter.setFont(btnFont);
        btnUserCenter.setPreferredSize(btnSize);
        add(btnUserCenter, gbc);

        // 功能按钮...
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        buttonPanel.setBackground(Color.WHITE);

        btnUserManage = new JButton("用户管理");
        btnStudent = new JButton("学生学籍管理");
        btnCourse = new JButton("选课系统");
        btnLibrary = new JButton("图书馆");
        btnShop = new JButton("商店");
        btnDorm = new JButton("宿舍");

        JButton[] buttons = {btnUserManage, btnStudent, btnCourse, btnLibrary, btnShop, btnDorm};
        for (JButton btn : buttons) {
            btn.setFont(btnFont);
            btn.setPreferredSize(btnSize);
            btn.setFocusPainted(false);
            buttonPanel.add(btn);
        }

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // 事件监听
        btnUserManage.addActionListener(e -> attemptEnterUserManagement());
        btnUserCenter.addActionListener(e -> attemptEnterUserCenter());
        btnStudent.addActionListener(e -> attemptEnterStudent());
        btnLibrary.addActionListener(e -> attemptEnterLibrary());
        btnCourse.addActionListener(e -> attemptEnterCourse());
        btnShop.addActionListener(e -> attemptEnterShop());
        btnDorm.addActionListener(e -> attemptEnterDorm());
    }

    private void attemptEnterUserCenter() {
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.showUserCenterPanel();
    }

    private void attemptEnterUserManagement() {
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.showUserManagementPanel();
    }

    private void attemptEnterStudent() {
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.showStudentPanel(); // 取消注释并调用
    }

    private void attemptEnterLibrary() {
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.showLibraryPanel();
    }

    private void attemptEnterCourse() {
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.showCoursePanel();
    }

    private void attemptEnterShop() {
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.showShopPanel();
    }

    private void attemptEnterDorm() {
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.showDormPanel();
    }

    @Override
    public void refreshPanel(User user) {
        if (user != null) {
            btnUserCenter.setText("个人中心 (" + user.getName() + ")");
        } else {
            btnUserCenter.setText("个人中心 (未登录)");
        }
    }

    @Override
    public String getPanelName() {
        return "MAIN";
    }
}