package com.seu.vcampus.common.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class ImageLoader {
    // 默认错误图片路径
    private static final String DEFAULT_ERROR_IMAGE = "/images/default_book.jpg";

    public static ImageIcon loadImage(String path) {
        ImageIcon icon = null;

        // 1. 尝试加载指定路径的图片
        if (path != null && !path.isEmpty()) {
            // 尝试作为文件路径加载
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                icon = new ImageIcon(path);
            }

            // 如果文件加载失败，尝试作为资源路径加载
            if (icon == null || icon.getIconWidth() <= 0) {
                URL resourceUrl = ImageLoader.class.getResource(path);
                if (resourceUrl != null) {
                    icon = new ImageIcon(resourceUrl);
                }
            }

            // 如果资源加载失败，尝试作为绝对资源路径加载
            if ((icon == null || icon.getIconWidth() <= 0) && !path.startsWith("/")) {
                 URL resourceUrl = ImageLoader.class.getResource("/" + path);
                if (resourceUrl != null) {
                    icon = new ImageIcon(resourceUrl);
                }
            }
        }

        // 2. 如果加载失败，尝试加载默认错误图片
        if (icon == null || icon.getIconWidth() <= 0) {
            URL defaultResource = ImageLoader.class.getResource(DEFAULT_ERROR_IMAGE);
            if (defaultResource != null) {
                icon = new ImageIcon(defaultResource);
            }
        }

        // 3. 如果默认错误图片也加载失败，创建简单的错误图标
        if (icon == null || icon.getIconWidth() <= 0) {
            // 创建空白图像
            int width = 200, height = 200;
            Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D) image.getGraphics();

            // 设置背景色
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, width, height);

            // 绘制错误标记
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(5));
            g.drawLine(10, 10, width - 10, height - 10);
            g.drawLine(width - 10, 10, 10, height - 10);

            // 添加文字
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            String text = "图片加载失败";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (width - textWidth) / 2;
            int y = height / 2 + fm.getAscent() / 2;
            g.drawString(text, x, y);

            g.dispose();
            icon = new ImageIcon(image);
        }

        // 4. 缩放图片到合适大小
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}