package com.seu.vcampus.client.view.panel.library;

import com.seu.vcampus.client.controller.LibraryController;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.client.view.panel.LibraryPanel;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LibraryMainPanel extends JPanel {


    // 界面组件

    private JLabel statusLabel;

    private JTabbedPane tabbedPane;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JButton returnButton;
    private User currentUser;

    private void initToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT)); // 右对齐

        returnButton = new JButton("返回主界面");
        returnButton.addActionListener(this::handleReturnAction);
        toolBar.add(returnButton);

        add(toolBar, BorderLayout.NORTH); // 添加到顶部
    }
    private void handleReturnAction(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            MainFrame.getInstance().showMainPanel(currentUser);
        });
    }

    public LibraryMainPanel(User currentUser) {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        this.currentUser=currentUser;
        // 添加主面板（初始为空）
        mainPanel.add(new JPanel(), "MAIN");

        add(mainPanel, BorderLayout.CENTER);
        initToolbar();
        showMainPanel();
    }

    private void showMainPanel() {
        JPanel mainContent = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // 添加通用面板（所有用户都有）
        tabbedPane.addTab("图书检索", new BookSearchPanel(currentUser));
        tabbedPane.addTab("我的图书馆", new MyLibraryPanel(currentUser));

        // 管理员面板
        if ("ADMIN".equals(MainFrame.getInstance().getRoleToModule("LIBRARY"))) {
            tabbedPane.addTab("管理员界面", new AdminPanel());
        }
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof Refreshable) {
                ((Refreshable) selected).refresh();
            }
        });



        mainContent.add(tabbedPane, BorderLayout.CENTER);

        mainPanel.add(mainContent, "MAIN");

        // 显示主面板
        cardLayout.show(mainPanel, "MAIN");
    }

    public interface Refreshable {
        void refresh();
    }

}