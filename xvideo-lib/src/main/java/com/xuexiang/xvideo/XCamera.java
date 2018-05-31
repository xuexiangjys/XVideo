package com.xuexiang.xvideo;

import android.text.TextUtils;

import com.xuexiang.xvideo.jniinterface.FFmpegBridge;

import java.io.File;

/**
 *
 *
 * @author xuexiang
 * @since 2018/5/30 下午8:40
 */
public class XCamera {
    /**
     * 应用包名
     */
    private static String mPackageName;
    /**
     * 应用版本名称
     */
    private static String mAppVersionName;
    /**
     * 应用版本号
     */
    private static int mAppVersionCode;
    /**
     * 视频缓存路径
     */
    private static String mVideoCachePath;

    /**
     * 执行FFMPEG命令保存路径
     */
    public final static String FFMPEG_LOG_FILENAME_TEMP = "jx_ffmpeg.log";

    /**
     * @param debug   debug模式
     * @param logPath 命令日志存储地址
     */
    public static void initialize(boolean debug, String logPath) {
        if (debug && TextUtils.isEmpty(logPath)) {
            logPath = mVideoCachePath + "/" + FFMPEG_LOG_FILENAME_TEMP;
        } else if (!debug) {
            logPath = null;
        }
        FFmpegBridge.initJXFFmpeg(debug, logPath);
    }


    /**
     * 获取视频缓存文件夹
     */
    public static String getVideoCachePath() {
        testInitialize();
        return mVideoCachePath;
    }

    /**
     * 设置视频缓存路径
     */
    public static void setVideoCachePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        mVideoCachePath = path;
    }

    private static void testInitialize() {
        if (mVideoCachePath == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 XCamera.setVideoCachePath() 初始化视频存放的路径！");
        }
    }
}
