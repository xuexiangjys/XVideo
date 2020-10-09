package com.xuexiang.xvideo;

import android.text.TextPaint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * 字符串工具类
 */
final class StringUtils {

    public static final String EMPTY = "";

    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
    /**
     * 用于生成文件
     */
    private static final String DEFAULT_FILE_PATTERN = "yyyy-MM-dd-HH-mm-ss";
    private static final double KB = 1024.0;
    private static final double MB = 1048576.0;
    private static final double GB = 1073741824.0;
    public static final SimpleDateFormat DATE_FORMAT_PART = new SimpleDateFormat(
            "HH:mm");

    /**
     * 格式化日期字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String formatDate(long date) {
        return formatDate(new Date(date), DEFAULT_DATE_PATTERN);
    }

    /**
     * 获取当前时间 格式为yyyy-MM-dd 例如2011-07-08
     *
     * @return
     */
    public static String getDate() {
        return formatDate(new Date(), DEFAULT_DATE_PATTERN);
    }


    /**
     * 拼接数组
     *
     * @param array
     * @param separator
     * @return
     */
    public static String join(final ArrayList<String> array,
                              final String separator) {
        StringBuffer result = new StringBuffer();
        if (array != null && array.size() > 0) {
            for (String str : array) {
                result.append(str);
                result.append(separator);
            }
            result.delete(result.length() - 1, result.length());
        }
        return result.toString();
    }

    public static String join(final Iterator<String> iter,
                              final String separator) {
        StringBuffer result = new StringBuffer();
        if (iter != null) {
            while (iter.hasNext()) {
                String key = iter.next();
                result.append(key);
                result.append(separator);
            }
            if (result.length() > 0) {
                result.delete(result.length() - 1, result.length());
            }
        }
        return result.toString();
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0 || "null".equalsIgnoreCase(str);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * @param str
     * @return
     */
    public static String trim(String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * 截取字符串
     *
     * @param search       待搜索的字符串
     * @param start        起始字符串 例如：<title>
     * @param end          结束字符串 例如：</title>
     * @param defaultValue
     * @return
     */
    public static String substring(String search, String start, String end,
                                   String defaultValue) {
        int start_len = start.length();
        int start_pos = StringUtils.isEmpty(start) ? 0 : search.indexOf(start);
        if (start_pos > -1) {
            int end_pos = StringUtils.isEmpty(end) ? -1 : search.indexOf(end,
                    start_pos + start_len);
            if (end_pos > -1) {
                return search.substring(start_pos + start.length(), end_pos);
            } else {
                return search.substring(start_pos + start.length());
            }
        }
        return defaultValue;
    }

    /**
     * 截取字符串
     *
     * @param search 待搜索的字符串
     * @param start  起始字符串 例如：<title>
     * @param end    结束字符串 例如：</title>
     * @return
     */
    public static String substring(String search, String start, String end) {
        return substring(search, start, end, "");
    }

    /**
     * 拼接字符串
     *
     * @param strs
     * @return
     */
    public static String concat(String... strs) {
        StringBuffer result = new StringBuffer();
        if (strs != null) {
            for (String str : strs) {
                if (str != null) {
                    result.append(str);
                }
            }
        }
        return result.toString();
    }

    /**
     * Helper function for making null strings safe for comparisons, etc.
     *
     * @return (s = = null) ? "" : s;
     */
    public static String makeSafe(String s) {
        return (s == null) ? "" : s;
    }
}
