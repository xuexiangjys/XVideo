package com.xuexiang.xvideo;

import java.io.File;

final class FileUtils {

    /**
     * 检测文件是否可用
     */
    public static boolean checkFile(File f) {
        return f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0));
    }

    /**
     * 删除目录
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(final String dirPath) {
        return deleteDir(getFileByPath(dirPath));
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(final File dir) {
        if (dir == null) return false;
        // 目录不存在返回 true
        if (!dir.exists()) return true;
        // 不是目录返回 false
        if (!dir.isDirectory()) return false;
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * 删除文件
     *
     * @param srcFilePath 文件路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(final String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }

    public static void deleteCacheFile(String f) {
        if (f != null && f.length() > 0) {
            File files = new File(f);
            if (files.exists() && files.isDirectory()) {
                for (File file : files.listFiles()) {
                    if (!file.isDirectory() && (file.getName().contains(".ts") || file.getName().contains("temp"))) {
                        file.delete();
                    }

                }
            }
        }
    }

    public static void deleteCacheFile2TS(String f) {
        if (f != null && f.length() > 0) {
            File files = new File(f);
            if (files.exists() && files.isDirectory()) {
                for (File file : files.listFiles()) {
                    if (!file.isDirectory() && (file.getName().contains(".ts"))) {
                        file.delete();
                    }

                }
            }
        }
    }

}
