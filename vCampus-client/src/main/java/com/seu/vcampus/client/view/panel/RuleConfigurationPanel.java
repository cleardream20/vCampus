// RuleConfigurationPanel.java
package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.AdminController;
import com.seu.vcampus.common.model.CourseSelectionRule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class RuleConfigurationPanel extends JPanel {
    private AdminController adminController;

    private JTextField batchNameField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JSpinner maxCreditsSpinner;
    private JTextArea prerequisitesArea;
    private JCheckBox conflictCheckBox;
    private JButton saveButton;

    public RuleConfigurationPanel(AdminController adminController) {
        this.adminController = adminController;
        initComponents();
        loadCurrentRule();
    }

    private void initComponents() {
        setLayout(new GridLayout(7, 2, 5, 5));

        add(new JLabel("批次名称:"));
        batchNameField = new JTextField();
        add(batchNameField);

        add(new JLabel("开始时间:"));
        startTimeField = new JTextField("yyyy-MM-dd HH:mm");
        add(startTimeField);

        add(new JLabel("结束时间:"));
        endTimeField = new JTextField("yyyy-MM-dd HH:mm");
        add(endTimeField);

        add(new JLabel("学分上限:"));
        maxCreditsSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 50, 1));
        add(maxCreditsSpinner);

        add(new JLabel("先修课程要求:"));
        prerequisitesArea = new JTextArea(3, 20);
        add(new JScrollPane(prerequisitesArea));

        add(new JLabel("冲突检测:"));
        conflictCheckBox = new JCheckBox("启用冲突检测");
        add(conflictCheckBox);

        add(new JLabel());
        saveButton = new JButton("保存配置");
        add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRule();
            }
        });
    }

    private void loadCurrentRule() {
        CourseSelectionRule rule = adminController.getRule();
        if (rule != null) {
            batchNameField.setText(rule.getBatchName());
            startTimeField.setText(rule.getStartTime().toString());
            endTimeField.setText(rule.getEndTime().toString());
            maxCreditsSpinner.setValue(rule.getMaxCredits());
            prerequisitesArea.setText(rule.getPrerequisites());
            conflictCheckBox.setSelected(rule.getConflictCheck());
        }
    }

    private void saveRule() {
        CourseSelectionRule rule = new CourseSelectionRule();
        rule.setBatchName(batchNameField.getText());
        rule.setStartTime(LocalDateTime.parse(startTimeField.getText()));
        rule.setEndTime(LocalDateTime.parse(endTimeField.getText()));
        rule.setMaxCredits((Integer) maxCreditsSpinner.getValue());
        rule.setPrerequisites(prerequisitesArea.getText());
        rule.setConflictCheck(conflictCheckBox.isSelected());

        if (adminController.configureRule(rule)) {
            JOptionPane.showMessageDialog(this, "规则配置成功");
        } else {
            JOptionPane.showMessageDialog(this, "规则配置失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}