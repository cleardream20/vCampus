package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel implements NavigatablePanel {
    private JButton btnUser;
    private JButton btnStudent;
    private JButton btnCourse;
    private JButton btnLibrary;
    private JButton btnShop;

    private JLabel lblTitle;
    private JPanel userCenterPanel;
    private JPanel subPanels;


    public MainPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // 标题
        JLabel lblTitle = new JLabel("vCampus服务大厅", SwingConstants.CENTER);
        lblTitle.setFont(new Font("微软雅黑", Font.BOLD, 18));
        lblTitle.setOpaque(true);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // 用户中心按钮
        JPanel userCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        btnUser = new JButton("您好！请登陆 ");
        btnUser.setFont(new Font("微软雅黑",  Font.BOLD, 18));
        btnUser.setPreferredSize(new Dimension(90, 30));

        userCenterPanel.add(btnUser);
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(userCenterPanel, gbc);

        // 子界面按钮
        JPanel subPanels  = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        Font commonFont = new Font("微软雅黑", Font.BOLD, 18);
        Dimension commonDimension = new Dimension(90, 30);
        btnStudent = new JButton("学生学籍管理");
        btnStudent.setFont(commonFont);
        btnStudent.setPreferredSize(commonDimension);

        btnCourse = new JButton("选课系统");
        btnCourse.setFont(commonFont);
        btnCourse.setPreferredSize(commonDimension);

        btnLibrary = new JButton("图书馆");
        btnLibrary.setFont(commonFont);
        btnLibrary.setPreferredSize(commonDimension);

        btnShop = new JButton("商店");
        btnShop.setFont(commonFont);
        btnShop.setPreferredSize(commonDimension);

        // 确保正确添加事件监听器
        btnShop.addActionListener(e -> {
            System.out.println("商店按钮被点击");
            openStore();
        });

        subPanels.add(btnStudent);
        subPanels.add(btnCourse);
        subPanels.add(btnLibrary);
        subPanels.add(btnShop);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(subPanels, gbc);

        // 事件监听
        btnUser.addActionListener(e -> attemptEnterUserCenter());
        btnStudent.addActionListener(e -> attemptEnterStudent());
    }

    private void openStore() {
        System.out.println("尝试打开商店");
        try {
            MainFrame mainFrame = MainFrame.getInstance();
            if (mainFrame.getCurrentUser() != null) {
                mainFrame.showShopPanel();
            } else {
                JOptionPane.showMessageDialog(this,
                        "请先登录系统",
                        "提示",
                        JOptionPane.WARNING_MESSAGE);
                mainFrame.showLoginPanel();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "打开商店时发生错误: " + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptEnterUserCenter() {
        // 安全地获取 MainFrame 实例
        MainFrame mainFrame = MainFrame.getInstance();
        User currentUser = mainFrame.getCurrentUser();

        if (currentUser != null) {
            mainFrame.showUserCenterPanel(currentUser);
        } else {
            JOptionPane.showMessageDialog(this,
                    "请先登录系统",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            mainFrame.showLoginPanel();
        }
    }

    private void openShop() {
        // 安全地获取 MainFrame 实例
        MainFrame mainFrame = MainFrame.getInstance();

        if (mainFrame.getCurrentUser() != null) {
            mainFrame.showShopPanel();
        } else {
            JOptionPane.showMessageDialog(this,
                    "请先登录系统",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            mainFrame.showLoginPanel();
        }
    }

    private void attemptEnterStudent() {
//        MainFrame mainFrame = MainFrame.getInstance();
//        mainFrame.showStudentPanel();
    }

    @Override
    public void refreshPanel(User user) {

    }

    @Override
    public String getPanelName() {
        return "MAIN";
    }

    public void setCurrentUser(User user) {
    }


}

