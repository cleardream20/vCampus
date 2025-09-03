package com.seu.vcampus.client.view.panel.StudentPanel;

import com.seu.vcampus.client.view.panel.StudentPanel.STPanel;
import javax.swing.*;

public class panelTest {
    public static void main(String[] args) {
        STPanel panel = new STPanel();
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
    }
}
