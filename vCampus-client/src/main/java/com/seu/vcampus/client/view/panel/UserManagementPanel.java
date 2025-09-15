// File: src/main/java/com/seu/vcampus/client/view/panel/UserManagementPanel.java
package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.common.model.Admin;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.Teacher;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserManagementPanel extends JPanel implements NavigatablePanel {

    private static final Font DEFAULT_FONT = new Font("微软雅黑", Font.PLAIN, 14);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainContentPanel = new JPanel(cardLayout);

    // 侧边栏按钮
    private final JButton btnUserManagement = new JButton("用户管理");
    private final JButton btnRequestHandling = new JButton("请求处理");
    private final JButton btnBack = new JButton("返回");

    // 用户管理页组件
    private final JPanel userPanel = new JPanel(new BorderLayout());
    private final JTabbedPane userTabs = new JTabbedPane();
    private final List<JTable> userTables = new ArrayList<>();
    private final List<List<User>> userDataList = new ArrayList<>(); // 每个tab对应的数据

    // 请求处理页组件
    private final JPanel requestPanel = new JPanel(new BorderLayout());
    private final JTabbedPane requestTabs = new JTabbedPane();
    private final JTable registerRequestTable = new JTable();
    private final JTable modifyRequestTable = new JTable();

    // 当前选中的用户
    private User selectedUser;

    public UserManagementPanel() {
        setLayout(new BorderLayout());
        initializeUI();
        setupEventHandlers();
    }

    private void initializeUI() {
        // 左侧导航栏
        JPanel sidebar = new JPanel(new GridLayout(3, 1, 0, 10));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.setPreferredSize(new Dimension(150, 0));
        btnUserManagement.setFont(DEFAULT_FONT);
        btnRequestHandling.setFont(DEFAULT_FONT);
        btnBack.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));

        sidebar.add(btnUserManagement);
        sidebar.add(btnRequestHandling);
        sidebar.add(Box.createVerticalStrut(50)); // 留空
        sidebar.add(btnBack);

        // 主内容区域
        mainContentPanel.add(userPanel, "USER");
        mainContentPanel.add(requestPanel, "REQUEST");

        // 初始化用户管理页
        initUserManagementPage();
        // 初始化请求处理页
        initRequestHandlingPage();

        add(sidebar, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void initUserManagementPage() {
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建三个标签页
        String[] tabs = {"学生", "教师", "管理员"};
        String[][] studentCols = {{"一卡通号", "cid"}, {"学号", "tsid"}, {"姓名", "name"}};
        String[][] teacherCols = {{"一卡通号", "cid"}, {"教职工号", "tsid"}, {"姓名", "name"}};
        String[][] adminCols = {{"一卡通号", "cid"}, {"账号", "tsid"}, {"姓名", "name"}};
        String[][][] columns = {studentCols, teacherCols, adminCols};

        for (int i = 0; i < 3; i++) {
            JPanel tabPanel = new JPanel(new BorderLayout());
            List<User> data = generateMockUserData(tabs[i]); // 模拟数据
            userDataList.add(data);

            Object[][] rowData = data.stream()
                    .map(u -> new Object[]{u.getCid(), u.getTsid(), u.getName()})
                    .toArray(Object[][]::new);
            String[] colNames = columns[i].length > 0 ? new String[]{columns[i][0][0], columns[i][1][0], columns[i][2][0]} : new String[]{};

            DefaultTableModel model = new DefaultTableModel(rowData, colNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane scroll = new JScrollPane(table);
            tabPanel.add(scroll, BorderLayout.CENTER);

            // 按钮区域
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnAdd = new JButton("新增用户");
            JButton btnDeleteBatch = new JButton("批量删除");
            buttonPanel.add(btnAdd);
            buttonPanel.add(btnDeleteBatch);

            tabPanel.add(buttonPanel, BorderLayout.SOUTH);

            userTabs.addTab(tabs[i], tabPanel);
            userTabs.setComponentAt(i, tabPanel);
            userTables.add(table);

            // 事件
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        int row = table.getSelectedRow();
                        if (row != -1) {
                            selectedUser = data.get(row);
                            showUserInfoDialog(false, selectedUser, tabs[i], (u) -> {
                                // 更新数据
                                int idx = data.indexOf(selectedUser);
                                if (idx != -1) data.set(idx, u);
                                ((DefaultTableModel) table.getModel()).setValueAt(u.getCid(), row, 0);
                                ((DefaultTableModel) table.getModel()).setValueAt(u.getTsid(), row, 1);
                                ((DefaultTableModel) table.getModel()).setValueAt(u.getName(), row, 2);
                                selectedUser = u;
                            });
                        }
                    }
                }
            });

            btnAdd.addActionListener(e -> {
                User newUser = new User();
                newUser.setRole(getRoleCode(tabs[i]));
                showUserInfoDialog(true, newUser, tabs[i], (u) -> {
                    data.add(0, u);
                    ((DefaultTableModel) table.getModel()).insertRow(0, new Object[]{u.getCid(), u.getTsid(), u.getName()});
                });
            });

            btnDeleteBatch.addActionListener(e -> {
                JCheckBox[] checkboxes = data.stream()
                        .map(u -> new JCheckBox(u.getName()))
                        .toArray(JCheckBox[]::new);
                JOptionPane pane = new JOptionPane(new JScrollPane(new JPanel() {{
                    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                    for (JCheckBox cb : checkboxes) add(cb);
                }}), JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
                JDialog dialog = pane.createDialog("选择要删除的用户");
                dialog.setVisible(true);
                Object result = pane.getValue();
                if (result.equals(JOptionPane.OK_OPTION)) {
                    List<User> toRemove = new ArrayList<>();
                    for (int j = 0; j < checkboxes.length; j++) {
                        if (checkboxes[j].isSelected()) toRemove.add(data.get(j));
                    }
                    data.removeAll(toRemove);
                    refreshUserTable(table, data);
                }
            });
        }

        userPanel.add(userTabs, BorderLayout.CENTER);
    }

    private void initRequestHandlingPage() {
        requestPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 模拟请求数据
        List<RequestItem> registerRequests = generateMockRegisterRequests();
        List<RequestItem> modifyRequests = generateMockModifyRequests();

        // 注册请求表
        Object[][] regData = registerRequests.stream()
                .map(r -> new Object[]{r.cid, r.name})
                .toArray(Object[][]::new);
        DefaultTableModel regModel = new DefaultTableModel(regData, new String[]{"一卡通号", "姓名"}) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        registerRequestTable.setModel(regModel);
        JScrollPane regScroll = new JScrollPane(registerRequestTable);

        // 修改请求表
        Object[][] modData = modifyRequests.stream()
                .map(r -> new Object[]{r.cid, r.name})
                .toArray(Object[][]::new);
        DefaultTableModel modModel = new DefaultTableModel(modData, new String[]{"一卡通号", "姓名"}) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        modifyRequestTable.setModel(modModel);
        JScrollPane modScroll = new JScrollPane(modifyRequestTable);

        // 添加按钮
        JPanel regBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        regBtnPanel.add(new JButton("批量通过"));
        regBtnPanel.add(new JButton("批量拒绝"));

        JPanel modBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modBtnPanel.add(new JButton("批量通过"));
        modBtnPanel.add(new JButton("批量拒绝"));

        // 注册 Tab
        JPanel regTab = new JPanel(new BorderLayout());
        regTab.add(regScroll, BorderLayout.CENTER);
        regTab.add(regBtnPanel, BorderLayout.SOUTH);

        // 修改 Tab
        JPanel modTab = new JPanel(new BorderLayout());
        modTab.add(modScroll, BorderLayout.CENTER);
        modTab.add(modBtnPanel, BorderLayout.SOUTH);

        requestTabs.addTab("注册请求", regTab);
        requestTabs.addTab("修改请求", modTab);

        // 事件绑定
        registerRequestTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = registerRequestTable.getSelectedRow();
                if (row != -1) {
                    RequestItem req = registerRequests.get(row);
                    showRequestDetailDialog("注册请求详情", req.details);
                }
            }
        });

        // 同理可扩展修改请求

        requestPanel.add(requestTabs, BorderLayout.CENTER);
    }

    // 显示用户信息对话框（查看/修改）
    private void showUserInfoDialog(boolean isNew, User user, String role, java.util.function.Consumer<User> onSave) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), isNew ? "新增用户" : "查看/修改用户", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<JComponent> fields = new ArrayList<>();
        List<JLabel> labels = new ArrayList<>();
        int row = 0;

        // 公共字段
        addField(dialog, gbc, "一卡通号:", user.getCid(), fields, labels, row++, !isNew);
        addField(dialog, gbc, "姓名:", user.getName(), fields, labels, row++, false);
        addField(dialog, gbc, "性别:", user.getGender(), fields, labels, row++, false);
        addField(dialog, gbc, "年龄:", String.valueOf(user.getAge()), fields, labels, row++, false);
        addField(dialog, gbc, "出生日期:", user.getBirthDate() != null ? user.getBirthDate().format(DATE_FORMATTER) : "", fields, labels, row++, false);
        addField(dialog, gbc, "电话号码:", user.getPhone(), fields, labels, row++, false);
        addField(dialog, gbc, "电子邮箱:", user.getEmail(), fields, labels, row++, false);
        addField(dialog, gbc, "家庭地址:", user.getAddress(), fields, labels, row++, false);
        addField(dialog, gbc, "身份证号:", user.getIdNumber(), fields, labels, row++, false);

        if ("学生".equals(role)) {
            addField(dialog, gbc, "学号:", user.getTsid(), fields, labels, row++, !isNew);
            addField(dialog, gbc, "入学时间:", user.getEnrollDate() != null ? user.getEnrollDate().format(DATE_FORMATTER) : "", fields, labels, row++, false);
            addField(dialog, gbc, "年级:", user.getGrade(), fields, labels, row++, false);
            addField(dialog, gbc, "学院:", user.getDepartment(), fields, labels, row++, false);
        } else if ("教师".equals(role)) {
            addField(dialog, gbc, "教职工号:", user.getTsid(), fields, labels, row++, !isNew);
            addField(dialog, gbc, "入职时间:", user.getHireDate() != null ? user.getHireDate().format(DATE_FORMATTER) : "", fields, labels, row++, false);
            addField(dialog, gbc, "职称:", user.getTitle(), fields, labels, row++, false);
            addField(dialog, gbc, "学院:", user.getDepartment(), fields, labels, row++, false);
        } else if ("管理员".equals(role)) {
            addField(dialog, gbc, "账号:", user.getTsid(), fields, labels, row++, !isNew);
        }

        // 按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("取消");
        JButton btnModify = new JButton("修改");
        JButton btnConfirm = new JButton("确认");
        btnConfirm.setVisible(false);

        btnModify.addActionListener(e -> {
            setFieldsEditable(fields, true);
            btnModify.setVisible(false);
            btnConfirm.setVisible(true);
        });

        btnConfirm.addActionListener(e -> {
            // 保存逻辑
            try {
                user.setCid(((JTextField) fields.get(0)).getText());
                user.setName(((JTextField) fields.get(1)).getText());
                user.setGender(((JTextField) fields.get(2)).getText());
                user.setAge(Integer.parseInt(((JTextField) fields.get(3)).getText()));
                user.setBirthDate(LocalDate.parse(((JTextField) fields.get(4)).getText(), DATE_FORMATTER));
                user.setPhone(((JTextField) fields.get(5)).getText());
                user.setEmail(((JTextField) fields.get(6)).getText());
                user.setAddress(((JTextField) fields.get(7)).getText());
                user.setIdNumber(((JTextField) fields.get(8)).getText());

                if ("学生".equals(role)) {
                    user.setTsid(((JTextField) fields.get(9)).getText());
                    user.setEndate(LocalDate.parse(((JTextField) fields.get(10)).getText(), DATE_FORMATTER));
                    user.setGrade(((JTextField) fields.get(11)).getText());
                    user.setDepartment(((JTextField) fields.get(12)).getText());
                } else if ("教师".equals(role)) {
                    user.setTsid(((JTextField) fields.get(9)).getText());
                    user.setHireDate(LocalDate.parse(((JTextField) fields.get(10)).getText(), DATE_FORMATTER));
                    user.setTitle(((JTextField) fields.get(11)).getText());
                    user.setDepartment(((JTextField) fields.get(12)).getText());
                } else if ("管理员".equals(role)) {
                    user.setTsid(((JTextField) fields.get(9)).getText());
                }

                int confirm = JOptionPane.showConfirmDialog(dialog, "确定要保存更改吗？", "确认", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    onSave.accept(user);
                    setFieldsEditable(fields, false);
                    btnModify.setVisible(true);
                    btnConfirm.setVisible(false);
                    if (isNew) dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入格式错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnModify);
        btnPanel.add(btnConfirm);
        btnPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(btnPanel, gbc);

        // 初始状态
        if (!isNew) {
            setFieldsEditable(fields, false);
        } else {
            btnModify.setVisible(false);
        }

        dialog.setSize(500, Math.min(600, 80 + row * 40));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void addField(Container parent, GridBagConstraints gbc, String labelText, String value,
                          List<JComponent> fields, List<JLabel> labels, int row, boolean editable) {
        JLabel label = new JLabel(labelText);
        label.setFont(DEFAULT_FONT);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        parent.add(label, gbc);
        labels.add(label);

        JTextField field = new JTextField(value, 20);
        field.setFont(DEFAULT_FONT);
        field.setEditable(editable);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        parent.add(field, gbc);
        fields.add(field);
    }

    private void setFieldsEditable(List<JComponent> fields, boolean editable) {
        for (JComponent f : fields) {
            if (f instanceof JTextField) ((JTextField) f).setEditable(editable);
        }
    }

    private void showRequestDetailDialog(String title, String details) {
        JTextArea area = new JTextArea(details, 15, 40);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);

        JOptionPane.showMessageDialog(this, scroll, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private List<User> generateMockUserData(String role) {
        List<User> list = new ArrayList<>();
        Random rand = new Random();

        for (int i = 1; i <= 50; i++) {
            String baseId = String.format("%04d", i);

            if ("学生".equals(role)) {
                Student s = new Student();
                s.setCid("C" + baseId);
                s.setTsid("S" + baseId);
                s.setName("学生" + i);
                s.setPassword("123456"); // 模拟密码
                s.setSex(i % 2 == 0 ? "男" : "女");
                s.setBirthday("2000-05-" + String.format("%02d", (i % 28) + 1));
                s.setAddress("南京市江宁区东南大学路" + i + "号");
                s.setNid("320105200005" + String.format("%04d", i));
                s.setEndate("2020-09-01");
                s.setGrade("大三");
                s.setMajor("计算机科学与技术");
                s.setStid("STU" + baseId);
                s.setEs("4年");
                s.setEsState("在籍");
                s.setEmail("stu" + i + "@seu.edu.cn");
                s.setPhone("13800138" + baseId.substring(2));
                list.add(s);
            } else if ("教师".equals(role)) {
                Teacher t = new Teacher();
                t.setCid("T" + baseId);
                t.setTsid("E" + baseId);
                t.setName("教师" + i);
                t.setPassword("123456");
                t.setAge(30 + rand.nextInt(20));
                t.setGender(t.getAge() % 2 == 0 ? "男" : "女");
                t.setAddress("南京市玄武区成贤街" + i + "号");
                t.setNid("32010219" + String.format("%02d", 80 + (40 - t.getAge())) + "0101" + baseId);
                t.setEndate("2015-08-01");
                t.setTitle(i % 3 == 0 ? "教授" : i % 3 == 1 ? "副教授" : "讲师");
                t.setDepartment("计算机学院");
                t.setEmail("teacher" + i + "@seu.edu.cn");
                t.setPhone("13900139" + baseId.substring(2));
                list.add(t);
            } else if ("管理员".equals(role)) {
                Admin a = new Admin();
                a.setCid("A" + baseId);
                a.setTsid("ADM" + baseId);
                a.setName("管理员" + i);
                a.setPassword("123456");
                a.setEmail("admin" + i + "@seu.edu.cn");
                a.setPhone("13700137" + baseId.substring(2));
                a.setModules(List.of("用户管理", "课程管理", "成绩管理"));
                list.add(a);
            }
        }
        return list;
    }

    private List<RequestItem> generateMockRegisterRequests() {
        List<RequestItem> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            RequestItem r = new RequestItem();
            r.cid = "NEW" + i;
            r.name = "新用户" + i;
            r.details = "一卡通号: NEW" + i + "\n姓名: 新用户" + i + "\n身份: 学生\n电话: 1390013900" + i + "\n邮箱: new" + i + "@example.com";
            list.add(r);
        }
        return list;
    }

    private List<RequestItem> generateMockModifyRequests() {
        List<RequestItem> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            RequestItem r = new RequestItem();
            r.cid = "MOD" + i;
            r.name = "修改用户" + i;
            r.details = "原电话: 13800138000\n新电话: 1390013900" + i + "\n原邮箱: old@example.com\n新邮箱: new" + i + "@example.com";
            list.add(r);
        }
        return list;
    }

    private String getRoleCode(String role) {
        return switch (role) {
            case "学生" -> "ST";
            case "教师" -> "TC";
            case "管理员" -> "AD";
            default -> "ST";
        };
    }

    private void refreshUserTable(JTable table, List<User> data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (User u : data) {
            model.addRow(new Object[]{u.getCid(), u.getTsid(), u.getName()});
        }
    }

    private void setupEventHandlers() {
        btnUserManagement.addActionListener(e -> cardLayout.show(mainContentPanel, "USER"));
        btnRequestHandling.addActionListener(e -> cardLayout.show(mainContentPanel, "REQUEST"));
        btnBack.addActionListener(e -> MainFrame.getInstance().showMainPanel(null));
    }

    @Override
    public void refreshPanel(User user) {
        cardLayout.show(mainContentPanel, "USER"); // 默认显示用户管理
    }

    @Override
    public String getPanelName() {
        return "USER_MANAGEMENT";
    }

    // 内部类：请求项
    private static class RequestItem {
        String cid;
        String name;
        String details;
    }
}