package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.service.UserService;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.Teacher;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.model.UserRequest;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserManagementPanel extends JPanel implements NavigatablePanel {

    private final CardLayout mainLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(mainLayout);

    // 当前登录用户（用于权限判断）
    private User currentUser;

    // 服务层引用（模拟）
    private static final UserService userService = new UserService();

    // 导航相关
    private final JList<String> navList = new JList<>(new String[]{"用户管理", "请求处理"});
    private final JButton backButton = new JButton("← 返回");

    // 用户管理页组件
    private final JTabbedPane userTypeTabs = new JTabbedPane();
    private final DefaultListModel<User> studentModel = new DefaultListModel<>();
    private final DefaultListModel<User> teacherModel = new DefaultListModel<>();
    private final DefaultListModel<User> adminModel = new DefaultListModel<>();
    private final JList<User> studentList = new JList<>(studentModel);
    private final JList<User> teacherList = new JList<>(teacherModel);
    private final JList<User> adminList = new JList<>(adminModel);

    // 请求处理页组件
    private final DefaultListModel<UserRequest> registerReqModel = new DefaultListModel<>();
    private final DefaultListModel<UserRequest> modifyReqModel = new DefaultListModel<>();
    private final JList<UserRequest> registerReqList = new JList<>(registerReqModel);
    private final JList<UserRequest> modifyReqList = new JList<>(modifyReqModel);
    private final JTabbedPane requestTabs = new JTabbedPane();

    // 操作状态标志
    private boolean isBatchDeleteMode = false;
    private List<User> selectedForDeletion = new ArrayList<>();

    // 批量删除确认按钮
    private final JButton confirmDeleteButton = new JButton("确认删除");

    private static final int COLS = 30;

    public UserManagementPanel() {
        currentUser = MainFrame.getInstance().getCurrentUser();
        setLayout(new BorderLayout());
        initUI();
        loadData(); // 模拟加载数据
    }

    private void initUI() {
        // 左侧导航栏
        navList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        navList.setSelectedIndex(0);
        navList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = navList.getSelectedValue();
                if ("用户管理".equals(selected)) {
                    mainLayout.show(contentPanel, "USER_MANAGE");
                } else if ("请求处理".equals(selected)) {
                    mainLayout.show(contentPanel, "REQUEST_PROCESS");
                }
            }
        });

        JScrollPane navScrollPane = new JScrollPane(navList);
        navScrollPane.setPreferredSize(new Dimension(180, 0));

        // 返回按钮
        backButton.addActionListener(e -> MainFrame.getInstance().showMainPanel(currentUser));

        // 构建用户管理页
        buildUserManagementPage();

        // 构建请求处理页
        buildRequestProcessPage();

        // 添加到主内容区
        contentPanel.add(createTitledPanel("用户管理", createScrollable(userTypeTabs)), "USER_MANAGE");
        contentPanel.add(createTitledPanel("请求处理", createScrollable(requestTabs)), "REQUEST_PROCESS");

        // 布局组装
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(backButton);

        add(topBar, BorderLayout.NORTH);
        add(navScrollPane, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void buildUserManagementPage() {
        // 设置列表渲染器
        studentList.setCellRenderer(new UserListRenderer());
        teacherList.setCellRenderer(new UserListRenderer());
        adminList.setCellRenderer(new UserListRenderer());

        // 包装带滚动条的列表
        userTypeTabs.addTab("学生", new JScrollPane(studentList));
        userTypeTabs.addTab("教师", new JScrollPane(teacherList));
        // 仅0号管理员可以管理管理员
        if (currentUser.getCid().equals("000000000"))
            userTypeTabs.addTab("管理员", new JScrollPane(adminList));

        // 双击查看用户
        studentList.addMouseListener(new ListClickHandler());
        teacherList.addMouseListener(new ListClickHandler());
        adminList.addMouseListener(new ListClickHandler());

        // 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("新增用户");
        JButton deleteBatchButton = new JButton("批量删除");
        JButton cancelDeleteButton = new JButton("取消");

        cancelDeleteButton.setVisible(false); // 初始隐藏
        confirmDeleteButton.setVisible(false); // 初始隐藏

        addButton.addActionListener(e -> showAddUserDialog());
        deleteBatchButton.addActionListener(e -> {
            isBatchDeleteMode = true;
            selectedForDeletion.clear();
            updateListSelection();
            cancelDeleteButton.setVisible(true);
        });
        cancelDeleteButton.addActionListener(e -> {
            isBatchDeleteMode = false;
            selectedForDeletion.clear();
            updateListSelection();
            cancelDeleteButton.setVisible(false);
            confirmDeleteButton.setVisible(false);
        });

        confirmDeleteButton.addActionListener(e -> {
            if (!selectedForDeletion.isEmpty()) {
                int result = JOptionPane.showConfirmDialog(this,
                        "确定要删除选中的 " + selectedForDeletion.size() + " 个用户吗？",
                        "确认删除", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    for (User user : selectedForDeletion) {
                        userService.deleteUser(user.getCid());
                        removeUserFromAllLists(user.getCid());
                    }
                    selectedForDeletion.clear();
                    isBatchDeleteMode = false;
                    updateListSelection();
                    cancelDeleteButton.setVisible(false);
                    confirmDeleteButton.setVisible(false);
                    JOptionPane.showMessageDialog(this, "删除成功！");
                }
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteBatchButton);
        buttonPanel.add(cancelDeleteButton);
        buttonPanel.add(confirmDeleteButton);

        // 将按钮放在下方
        userTypeTabs.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("用户列表"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JPanel userManagePanel = new JPanel(new BorderLayout());
        userManagePanel.add(userTypeTabs, BorderLayout.CENTER);
        userManagePanel.add(buttonPanel, BorderLayout.SOUTH);

        // 替换默认的 userTypeTabs 容器
        int index = userTypeTabs.indexOfTab("学生");
        userTypeTabs.setComponentAt(index, wrapWithScrollPane(studentList));
        index = userTypeTabs.indexOfTab("教师");
        userTypeTabs.setComponentAt(index, wrapWithScrollPane(teacherList));
        if (currentUser.getCid().equals("000000000")) {
            index = userTypeTabs.indexOfTab("管理员");
            userTypeTabs.setComponentAt(index, wrapWithScrollPane(adminList));
        }

        // 重新添加按钮
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(userManagePanel, BorderLayout.CENTER);
    }

    private void buildRequestProcessPage() {
        registerReqList.setCellRenderer(new RequestListRenderer());
        modifyReqList.setCellRenderer(new RequestListRenderer());

        requestTabs.addTab("注册请求", new JScrollPane(registerReqList));
        requestTabs.addTab("修改请求", new JScrollPane(modifyReqList));

        registerReqList.addMouseListener(new RequestClickHandler(true));
        modifyReqList.addMouseListener(new RequestClickHandler(false));
    }

    private JScrollPane wrapWithScrollPane(JList<?> list) {
        return new JScrollPane(list,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private JScrollPane createScrollable(Component comp) {
        return new JScrollPane(comp);
    }

    private JPanel createTitledPanel(String title, Component content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    // 更新列表选择模式
    private void updateListSelection() {
        studentList.clearSelection();
        teacherList.clearSelection();
        adminList.clearSelection();

        if (isBatchDeleteMode) {
            studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            teacherList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            adminList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            teacherList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            adminList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    // 模拟加载数据
    private void loadData() {
        List<User> allUsers = userService.getAllUsers();
        for (User u : allUsers) {
            switch (u.getRole()) {
                case "ST": studentModel.addElement(userService.getStudentByUser(u)); break;
                case "TC": teacherModel.addElement(userService.getTeacherByUser(u)); break;
                case "AD": adminModel.addElement(userService.getAdminByUser(u)); break;
            }
        }

        // 加载请求（模拟）
        List<UserRequest> requests = userService.getPendingRequests(); // 模拟方法
        for (UserRequest req : requests) {
            if ("REGISTER".equals(req.getType())) {
                registerReqModel.addElement(req);
            } else if ("MODIFY".equals(req.getType())) {
                modifyReqModel.addElement(req);
            }
        }
    }

    // ========== 用户操作对话框 ==========

    private void showAddUserDialog() {
        UserInfoFormPanel form = new UserInfoFormPanel(null);
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "新增用户", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton okBtn = new JButton("确认");
        JButton cancelBtn = new JButton("取消");
        buttons.add(okBtn);
        buttons.add(cancelBtn);
        dialog.add(buttons, BorderLayout.SOUTH);

        okBtn.addActionListener(e -> {
            User newUser = form.getUserData();
            if (newUser != null) {
                // 模拟保存
                userService.addUser(newUser);
                // 刷新对应列表
                switch (newUser.getRole()) {
                    case "ST": studentModel.addElement(newUser); break;
                    case "TC": teacherModel.addElement(newUser); break;
                    case "AD": adminModel.addElement(newUser); break;
                }
                dialog.dispose();
                JOptionPane.showMessageDialog(dialog, "用户创建成功！");
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditUserDialog(User user) {
        UserInfoFormPanel form = new UserInfoFormPanel(user);
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "用户信息", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton editBtn = new JButton("修改");
        JButton confirmBtn = new JButton("确认");
        JButton cancelBtn = new JButton("取消");

        confirmBtn.setVisible(false);
        cancelBtn.setVisible(false);

        buttons.add(editBtn);
        buttons.add(confirmBtn);
        buttons.add(cancelBtn);
        dialog.add(buttons, BorderLayout.SOUTH);

        // 初始为只读
        form.setReadOnly(true);

        editBtn.addActionListener(e -> {
            form.setReadOnly(false);
            editBtn.setVisible(false);
            confirmBtn.setVisible(true);
            cancelBtn.setVisible(true);
        });

        confirmBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(dialog, "确定要修改此用户信息吗？", "确认", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                User updated = form.getUserData();
                if (updated != null) {
                    // 模拟更新
                    userService.updateUser(updated);
                    // 更新列表显示
                    updateUserInList(updated);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(dialog, "修改成功！");
                }
            }
        });

        cancelBtn.addActionListener(e -> {
            form.setReadOnly(true);
            editBtn.setVisible(true);
            confirmBtn.setVisible(false);
            cancelBtn.setVisible(false);
        });

        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateUserInList(User u) {
        removeUserFromAllLists(u.getCid());
        switch (u.getRole()) {
            case "ST": studentModel.addElement(userService.getStudentByUser(u)); break;
            case "TC": teacherModel.addElement(userService.getTeacherByUser(u)); break;
            case "AD": adminModel.addElement(userService.getAdminByUser(u)); break;
        }
    }

    private void removeUserFromAllLists(String cid) {
        for (int i = 0; i < studentModel.size(); i++) {
            if (Objects.equals(studentModel.getElementAt(i).getCid(), cid)) {
                studentModel.remove(i);
                break;
            }
        }
        for (int i = 0; i < teacherModel.size(); i++) {
            if (Objects.equals(teacherModel.getElementAt(i).getCid(), cid)) {
                teacherModel.remove(i);
                break;
            }
        }
        for (int i = 0; i < adminModel.size(); i++) {
            if (Objects.equals(adminModel.getElementAt(i).getCid(), cid)) {
                adminModel.remove(i);
                break;
            }
        }
    }

    // ========== 内部类：列表渲染器 ==========

    static class UserListRenderer extends JPanel implements ListCellRenderer<User> {
        private final JLabel infoLabel = new JLabel();
        private final JButton viewBtn = new JButton("查看/修改");

        public UserListRenderer() {
            setLayout(new BorderLayout());
            add(infoLabel, BorderLayout.CENTER);
            add(viewBtn, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends User> list, User value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                infoLabel.setText(value.getCid() + " - " + value.getName());
            }
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    static class RequestListRenderer extends JPanel implements ListCellRenderer<UserRequest> {
        private final JLabel label = new JLabel();
        private final JButton approveBtn = new JButton("✓");
        private final JButton rejectBtn = new JButton("✗");

        public RequestListRenderer() {
            setLayout(new BorderLayout());
            approveBtn.setPreferredSize(new Dimension(30, 25));
            rejectBtn.setPreferredSize(new Dimension(30, 25));
            add(label, BorderLayout.CENTER);
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            btnPanel.add(approveBtn);
            btnPanel.add(rejectBtn);
            add(btnPanel, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends UserRequest> list, UserRequest value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                label.setText(value.getCid() + " - " + value.getName());
            }
            if (isSelected) {
                setBackground(list.getSelectionBackground());
            } else {
                setBackground(list.getBackground());
            }
            setOpaque(true);
            return this;
        }
    }

    // ========== 事件监听器 ==========

    class ListClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JList<?> list = (JList<?>) e.getSource();
            int index = list.locationToIndex(e.getPoint());
            if (index >= 0) {
                User user = (User) list.getModel().getElementAt(index);

                if (isBatchDeleteMode) {
                    if (selectedForDeletion.contains(user)) {
                        selectedForDeletion.remove(user);
                    } else {
                        selectedForDeletion.add(user);
                    }
                    if (selectedForDeletion.size() > 0 && !confirmDeleteButton.isVisible()) {
                        confirmDeleteButton.setVisible(true);
                    }
                    if (selectedForDeletion.isEmpty()) {
                        confirmDeleteButton.setVisible(false);
                    }
                    return;
                }

                if (e.getClickCount() == 2) {
                    showEditUserDialog(user);
                }
            }
        }
    }

    class RequestClickHandler extends MouseAdapter {
        private final boolean isRegister;

        public RequestClickHandler(boolean isRegister) {
            this.isRegister = isRegister;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            JList<?> list = (JList<?>) e.getSource();
            int index = list.locationToIndex(e.getPoint());
            if (index >= 0) {
                UserRequest req = (UserRequest) list.getModel().getElementAt(index);
                Point p = e.getPoint();
                Rectangle bounds = list.getCellBounds(index, index);
                Object source = e.getSource();
                JComponent comp = (JComponent) source;
                Component inner = comp.findComponentAt(p.x - bounds.x, p.y - bounds.y);

                if (inner instanceof JButton) {
                    JButton btn = (JButton) inner;
                    if ("✓".equals(btn.getText())) {
                        // 同意
                        userService.approveRequest(req);
                        if (isRegister) {
                            registerReqModel.remove(index);
                        } else {
                            modifyReqModel.remove(index);
                        }
                        JOptionPane.showMessageDialog(UserManagementPanel.this, "已同意该请求");
                    } else if ("✗".equals(btn.getText())) {
                        // 拒绝
                        userService.rejectRequest(req);
                        if (isRegister) {
                            registerReqModel.remove(index);
                        } else {
                            modifyReqModel.remove(index);
                        }
                        JOptionPane.showMessageDialog(UserManagementPanel.this, "已拒绝该请求");
                    }
                }
            }
        }
    }

    // ========== 表单面板复用 ==========

    static class UserInfoFormPanel extends JPanel {
        private final User originalUser;
        private boolean isStudent, isTeacher, isAdministrator;

        // 公共字段
        private JTextField cidField, tsidField, nameField, genderField, ageField,
                birthDateField, phoneField, emailField, addressField, idCardField, departmentField;

        // 学生字段
        private JTextField gradeField, studentTypeField, enrollmentDateField;

        // 教师字段
        private JTextField titleField, hireDateField;

        public UserInfoFormPanel(User user) {
            this.originalUser = user;
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            initializeFields();

            if (user != null) {
                populateFields(user);
                String role = user.getRole();
                isStudent = "ST".equals(role);
                isTeacher = "TC".equals(role);
                isAdministrator = "AD".equals(role);
            } else {
                isStudent = isTeacher = isAdministrator = false;
            }

            int y = 0;
            addRow(gbc, "一卡通号:", cidField, y++);
            addRow(gbc, "姓名:", nameField, y++);
            addRow(gbc, "性别:", genderField, y++);
            addRow(gbc, "年龄:", ageField, y++);
            addRow(gbc, "出生日期:", birthDateField, y++);
            addRow(gbc, "电话号码:", phoneField, y++);
            addRow(gbc, "电子邮箱:", emailField, y++);
            addRow(gbc, "家庭地址:", addressField, y++);
            addRow(gbc, "身份证号:", idCardField, y++);
            addRow(gbc, "学院:", departmentField, y++);

            if (isStudent || (!isStudent && !isTeacher)) { // 新增时允许选角色
                addRow(gbc, "学号:", tsidField, y++);
                addRow(gbc, "入学时间:", enrollmentDateField, y++);
                addRow(gbc, "年级:", gradeField, y++);
            }
            if (isTeacher) {
                addRow(gbc, "教职工号:", tsidField, y++);
                addRow(gbc, "入职时间:", hireDateField, y++);
                addRow(gbc, "职称:", titleField, y++);
            }
            if (isAdministrator) {
                // 管理员仅显示部分
            }
        }

        private void initializeFields() {
            cidField = new JTextField(COLS);
            tsidField = new JTextField(COLS);
            nameField = new JTextField(COLS);
            genderField = new JTextField(COLS);
            ageField = new JTextField(COLS);
            birthDateField = new JTextField(COLS);
            phoneField = new JTextField(COLS);
            emailField = new JTextField(COLS);
            addressField = new JTextField(COLS);
            idCardField = new JTextField(COLS);
            departmentField = new JTextField(COLS);

            gradeField = new JTextField(COLS);
            studentTypeField = new JTextField(COLS);
            enrollmentDateField = new JTextField(COLS);

            titleField = new JTextField(COLS);
            hireDateField = new JTextField(COLS);

            setAllEditable(false);

            idCardField.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { updateBirthAndAge(); }
                @Override public void removeUpdate(DocumentEvent e) { updateBirthAndAge(); }
                @Override public void changedUpdate(DocumentEvent e) { updateBirthAndAge(); }
            });
        }

        private void updateBirthAndAge() {
            String id = idCardField.getText().trim();
            if (id.length() == 18) {
                try {
                    String b = id.substring(6, 14);
                    LocalDate birth = LocalDate.parse(b, DateTimeFormatter.ofPattern("yyyyMMdd"));
                    birthDateField.setText(birth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    ageField.setText(String.valueOf(Period.between(birth, LocalDate.now()).getYears()));
                } catch (Exception ignored) {}
            }
        }

        private void addRow(GridBagConstraints gbc, String label, JTextField field, int y) {
            gbc.gridx = 0;
            gbc.gridy = y;
            add(new JLabel(label), gbc);
            gbc.gridx = 1;
            add(field, gbc);
        }

        // 初始化/更新用户信息UserInfoFormPanel界面
        private void populateFields(User u) {
            cidField.setText(u.getCid());
            nameField.setText(u.getName());
            phoneField.setText(u.getPhone());
            emailField.setText(u.getEmail());
            tsidField.setText(u.getTsid());

            if ("ST".equals(u.getRole())) {
                Student st = userService.getStudentByUser(u);
                enrollmentDateField.setText(st.getEndate());
                gradeField.setText(st.getGrade());

                ageField.setText(String.valueOf(st.getAge()));
                genderField.setText(st.getSex());
                birthDateField.setText(st.getBirthday());
                addressField.setText(st.getAddress());
                idCardField.setText(st.getNid());
                departmentField.setText(st.getMajor());
                gradeField.setText(st.getGrade());
            } else if ("TC".equals(u.getRole())) {
                Teacher tc = userService.getTeacherByUser(u);
                hireDateField.setText(tc.getEndate());
                titleField.setText(tc.getTitle());

                ageField.setText(String.valueOf(tc.getAge()));
                genderField.setText(tc.getGender());
                addressField.setText(tc.getAddress());
                idCardField.setText(tc.getNid());
                departmentField.setText(tc.getDepartment());
            } // 管理员没有更多的了
        }

        public void setReadOnly(boolean readOnly) {
            phoneField.setEditable(!readOnly);
            emailField.setEditable(!readOnly);
            addressField.setEditable(!readOnly);
            idCardField.setEditable(!readOnly);
            departmentField.setEditable(!readOnly);
            if (isStudent) {
                gradeField.setEditable(!readOnly);
                studentTypeField.setEditable(!readOnly);
                enrollmentDateField.setEditable(!readOnly);
            } else if (isTeacher) {
                titleField.setEditable(!readOnly);
                hireDateField.setEditable(!readOnly);
            }
            // 一卡通号、姓名、性别、年龄、出生日期不可编辑
        }

        private void setAllEditable(boolean editable) {
            cidField.setEditable(false);
            nameField.setEditable(false);
            genderField.setEditable(false);
            ageField.setEditable(false);
            birthDateField.setEditable(false);
            tsidField.setEditable(false);
            setReadOnly(editable);
        }

        public User getUserData() {
            User u = new User();
            u.setCid(cidField.getText().trim());
            u.setName(nameField.getText().trim());
            u.setPhone(phoneField.getText().trim());
            u.setEmail(emailField.getText().trim());
//            u.setAddress(addressField.getText().trim());
//            u.setIdCard(idCardField.getText().trim());
//            u.setCollege(collegeField.getText().trim());
            u.setTsid(tsidField.getText().trim());

            // 推断角色
            if (originalUser != null) {
                u.setRole(originalUser.getRole());
            } else {
                // 新增逻辑中需让用户选择角色（此处简化）
                u.setRole(isStudent ? "ST" : isTeacher ? "TC" : "AD");
            }

            if ("ST".equals(u.getRole())) {
//                u.setEnrollmentDate(enrollmentDateField.getText().trim());
//                u.setGrade(gradeField.getText().trim());
//                u.setStudentType(studentTypeField.getText().trim());
            } else if ("TC".equals(u.getRole())) {
//                u.setHireDate(hireDateField.getText().trim());
//                u.setTitle(titleField.getText().trim());
            }

            // 校验必要字段
            if (u.getCid().isEmpty() || u.getName().isEmpty()) {
                JOptionPane.showMessageDialog(this, "一卡通号和姓名不能为空！");
                return null;
            }
            return u;
        }
    }

    @Override
    public void refreshPanel(User user) {
        this.currentUser = user;
        // 可在此刷新数据
        studentModel.clear();
        teacherModel.clear();
        adminModel.clear();
        registerReqModel.clear();
        modifyReqModel.clear();
        loadData();
    }

    @Override
    public String getPanelName() {
        return "USER_MANAGEMENT";
    }
}