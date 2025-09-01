package com.seu.vcampus.common.util;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImageLoader {
    public static ImageIcon loadImage(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        // 尝试从文件系统加载
        File file = new File(path);
        if (file.exists()) {
            return new ImageIcon(path);
        }

        // 尝试从资源加载
        ImageIcon icon = new ImageIcon(ImageLoader.class.getResource(path));
        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            return icon;
        }

        return null;
    }
}