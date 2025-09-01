# Java Swing

# JFrame

| JFrame组件 | 功能 |
|--|--|
| JDialog | 对话框 |
| JOptionPane | 对话框 |
| JButton | 按钮 |
| JCheckBox | 复选框 |
| JComBox | 下拉框 |
| JLabel | 标签 |
| JRadioButton | 单选按钮 |
| JList | 显示一组条目的组件 |
| JTextField | 文本框 |
| JPasswordField | 密码框 |
| JTextArea | 文本区域 |

## 新建JFrame对象
```java
JFrame f = new JFrame();
```

## 设置大小
```java
setSize(int width, int height);
setLocation(int x, int y);
setBounds(int a, int b, int w, int h); // 初始位置(a, b), 窗口大小w, h
setVisible(boolean b); // 设置窗口是否可见
setResizable(boolean b); // 设置窗口是否课调整大小
dispose(); // 撤销当前窗口，并释放资源

setExtendedState(int state); // 设置窗口的拓展状态
// state:
//MAXIMIZED_HORIZ // 水平方向最大化
//MAXIMIZED_VERT // 垂直方向最大化
//MAXIMIZED_BOTH // 都最大化
```

## 设置关闭方式
单击右上角关闭图标后会如何处理
```java
setDefaultCloseOperation(int operation);
// operation:
// DO_NOTING_ON_CLOSE // 什么也不做
// HIDE_ON_CLOSe // 隐藏当前窗口 & 释放资源
// EXIT_ON_CLOSE // 结束窗口所在的应用程序
```

## JDialog
小JFrame，但必须从属于JFrame

```java
JDia();
JDialog(Frame f);
JDialog(Frame f, String title);
```

## JPanel
面板


