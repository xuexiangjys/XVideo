package com.xuexiang.xvideo;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;

import com.xuexiang.xvideo.jniinterface.FFmpegBridge;
import com.xuexiang.xvideo.model.CompressResult;
import com.xuexiang.xvideo.model.LocalMediaConfig;
import com.xuexiang.xvideo.model.MediaRecorderConfig;

import java.io.File;

import static com.xuexiang.xvideo.MediaRecorderActivity.OVER_ACTIVITY_NAME;

/**
 * @author xuexiang
 * @since 2018/5/30 下午8:40
 */
public class XVideo {
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
            throw new ExceptionInInitializerError("请先在全局Application中调用 XVideo.setVideoCachePath() 初始化视频存放的路径！");
        }
    }

    //============================视频录制================================//

    /**
     * 开始视频录制
     *
     * @param context
     * @param overGOActivityName  录制结束后需要跳转的Activity全类名
     * @param mediaRecorderConfig
     */
    public static void startVideoRecorder(Activity context, String overGOActivityName, MediaRecorderConfig mediaRecorderConfig) {
        context.startActivity(new Intent(context, MediaRecorderActivity.class)
                .putExtra(OVER_ACTIVITY_NAME, overGOActivityName)
                .putExtra(MediaRecorderFragment.MEDIA_RECORDER_CONFIG_KEY, mediaRecorderConfig));
    }


    /**
     * 开始视频录制
     *
     * @param fragment
     * @param overGOActivityName  录制结束后需要跳转的Activity全类名
     * @param mediaRecorderConfig
     */
    public static void startVideoRecorder(Fragment fragment, String overGOActivityName, MediaRecorderConfig mediaRecorderConfig) {
        fragment.startActivity(new Intent(fragment.getContext(), MediaRecorderActivity.class)
                .putExtra(OVER_ACTIVITY_NAME, overGOActivityName)
                .putExtra(MediaRecorderFragment.MEDIA_RECORDER_CONFIG_KEY, mediaRecorderConfig));
    }

    /**
     * 开始视频录制
     *
     * @param context
     * @param mediaRecorderConfig
     * @param requestCode         请求码
     */
    public static void startVideoRecorder(Activity context, MediaRecorderConfig mediaRecorderConfig, int requestCode) {
        context.startActivityForResult(new Intent(context, MediaRecorderActivity.class)
                .putExtra(MediaRecorderFragment.MEDIA_RECORDER_CONFIG_KEY, mediaRecorderConfig), requestCode);
    }

    /**
     * 开始视频录制
     *
     * @param fragment
     * @param mediaRecorderConfig
     * @param requestCode         请求码
     */
    public static void startVideoRecorder(Fragment fragment, MediaRecorderConfig mediaRecorderConfig, int requestCode) {
        fragment.startActivityForResult(new Intent(fragment.getContext(), MediaRecorderActivity.class)
                .putExtra(MediaRecorderFragment.MEDIA_RECORDER_CONFIG_KEY, mediaRecorderConfig), requestCode);
    }

    //============================视频压缩================================//

    /**
     * 开始视频压缩 【比较耗时，需要放在子现场中执行】
     *
     * @param config 需要压缩的本地视频的配置
     * @return
     */
    @WorkerThread
    public static CompressResult startCompressVideo(LocalMediaConfig config) {
        return new LocalMediaCompress(config).startCompress();
    }


}
