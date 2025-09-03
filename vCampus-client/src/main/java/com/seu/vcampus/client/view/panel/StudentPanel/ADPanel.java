package com.seu.vcampus.client.view.panel.StudentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.seu.vcampus.client.controller.Student.ADController;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.User;

public class ADPanel extends JPanel implements NavigatablePanel {

    

    @Override
    public void refreshPanel(User user) {

    }

    @Override
    public String getPanelName() { return "StudentAD"; }
}
