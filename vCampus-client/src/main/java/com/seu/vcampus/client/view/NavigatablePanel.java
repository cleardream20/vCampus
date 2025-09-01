package com.seu.vcampus.client.view;

import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.User;

public interface NavigatablePanel {
    static void show(MainFrame mainFrame) {}

    void refreshPanel(User user);

    // name 用于 CardLayout标识
    String getPanelName();
}
