package com.xuexiang.xvideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.xuexiang.xvideo.model.MediaObject;
import com.xuexiang.xvideo.model.MediaRecorderConfig;

/**
 * 视频录制界面
 *
 * @author xuexiang
 * @since 2018/5/31 上午11:38
 */
public class MediaRecorderActivity extends AppCompatActivity implements MediaRecorderFragment.OnMediaRecorderListener {
    /**
     * 录制完成后需要跳转的activity
     */
    public final static String OVER_ACTIVITY_NAME = "over_activity_name";

    /**
     * 视屏地址
     */
    public final static String VIDEO_URI = "video_uri";
    /**
     * 本次视频保存的文件夹地址
     */
    public final static String OUTPUT_DIRECTORY = "output_directory";
    /**
     * 视屏截图地址
     */
    public final static String VIDEO_SCREENSHOT = "video_screenshot";

    private MediaRecorderFragment mMediaRecorderFragment;

    private String overActivityName = "";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaRecorderFragment.onCreateMediaRecorder(this);
        setContentView(R.layout.xvideo_activity_media_recorder);

        Intent intent = getIntent();
        if (intent != null) {
            MediaRecorderConfig mediaRecorderConfig = intent.getParcelableExtra(MediaRecorderFragment.MEDIA_RECORDER_CONFIG_KEY);
            overActivityName = intent.getStringExtra(OVER_ACTIVITY_NAME);
            if (mediaRecorderConfig == null) {
                mediaRecorderConfig = MediaRecorderConfig.newInstance();
            }

            mMediaRecorderFragment = MediaRecorderFragment.newInstance(mediaRecorderConfig, this);

            getSupportFragmentManager().beginTransaction().replace(R.id.fl_record_container, mMediaRecorderFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        mMediaRecorderFragment.onBackPressed();
    }

    /**
     * 取消视频录制
     */
    @Override
    public void onCancel() {
        finish();
    }

    /**
     * 视频录制成功
     *
     * @param mediaObject 录制视频的信息
     */
    @Override
    public void onRecordSuccess(MediaObject mediaObject) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(MediaRecorderActivity.OUTPUT_DIRECTORY, mediaObject.getOutputDirectory());
        bundle.putString(MediaRecorderActivity.VIDEO_URI, mediaObject.getOutputTempTranscodingVideoPath());
        bundle.putString(MediaRecorderActivity.VIDEO_SCREENSHOT, mediaObject.getOutputVideoThumbPath());
        intent.putExtras(bundle);
        if (StringUtils.isEmpty(overActivityName)) {
            setResult(RESULT_OK, intent);
        } else {
            try {
                intent.setClass(this, Class.forName(overActivityName));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("需要传入录制完成后跳转的Activity的全类名");
            }
            startActivity(intent);
        }
        finish();
    }

    /**
     * 视频录制失败
     *
     * @param msg 失败原因
     */
    @Override
    public void onRecordFailed(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        finish();
    }
}
