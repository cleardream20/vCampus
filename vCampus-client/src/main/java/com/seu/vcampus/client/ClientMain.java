package com.seu.vcampus.client;

import com.seu.vcampus.client.controller.LoginController;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.client.view.panel.LoginPanel;
import com.sun.tools.javac.Main;

import javax.swing.*;

public class ClientMain {
    public static void main(String[] args) {
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.setVisible(true);

    }
}
