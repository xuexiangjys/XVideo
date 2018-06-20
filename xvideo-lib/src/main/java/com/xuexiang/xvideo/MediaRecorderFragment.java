package com.xuexiang.xvideo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xuexiang.xvideo.model.MediaObject;
import com.xuexiang.xvideo.model.MediaRecorderConfig;

import java.io.File;

import static com.xuexiang.xvideo.model.MediaRecorderConfig.DEFAULT_RECORD_TIME_MAX;
import static com.xuexiang.xvideo.model.MediaRecorderConfig.DEFAULT_RECORD_TIME_MIN;

/**
 * 视频录制的fragment
 *
 * @author xuexiang
 * @since 2018/5/31 上午9:27
 */
public class MediaRecorderFragment extends Fragment implements
        MediaRecorderBase.OnErrorListener, View.OnClickListener, MediaRecorderBase.OnPreparedListener,
        MediaRecorderBase.OnEncodeListener {

    /**
     * 录制最短时间
     */
    private int recordTimeMin = DEFAULT_RECORD_TIME_MIN;
    /**
     * 录制最长时间
     */
    private int recordTimeMax = DEFAULT_RECORD_TIME_MAX;
    /**
     * 刷新进度条
     */
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    /**
     * 延迟拍摄停止
     */
    private static final int HANDLE_STOP_RECORD = 1;

    /**
     * 下一步
     */
    private ImageView mTitleNext;
    /**
     * 前后摄像头切换
     */
    private CheckBox mCameraSwitch;
    /**
     * 回删按钮、延时按钮、滤镜按钮
     */
    private CheckedTextView mRecordDelete;
    /**
     * 闪光灯
     */
    private CheckBox mRecordLed;
    /**
     * 拍摄按钮
     */
    private TextView mRecordController;

    /**
     * 底部条
     */
    private RelativeLayout mBottomLayout;
    /**
     * 摄像头数据显示画布
     */
    private SurfaceView mSurfaceView;
    /**
     * 录制进度
     */
    private ProgressView mProgressView;

    /**
     * SDK视频录制对象
     */
    private MediaRecorderBase mMediaRecorder;
    /**
     * 视频信息
     */
    private MediaObject mMediaObject;

    /**
     * 是否是点击状态
     */
    private volatile boolean mPressedStatus;
    /**
     * 录制配置key
     */
    public final static String MEDIA_RECORDER_CONFIG_KEY = "media_recorder_config_key";

    private boolean startState;
    private boolean needFullScreen = false;
    private RelativeLayout mTitleLayout;

    /**
     * 视频录制的监听
     */
    private OnMediaRecorderListener mOnMediaRecorderListener;

    /**
     * 创建视频录制的fragment
     *
     * @param mediaRecorderConfig
     * @return
     */
    public static MediaRecorderFragment newInstance(MediaRecorderConfig mediaRecorderConfig) {
        MediaRecorderFragment fragment = new MediaRecorderFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MEDIA_RECORDER_CONFIG_KEY, mediaRecorderConfig);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 创建视频录制的fragment
     *
     * @param mediaRecorderConfig
     * @return
     */
    public static MediaRecorderFragment newInstance(MediaRecorderConfig mediaRecorderConfig, OnMediaRecorderListener onMediaRecorderListener) {
        MediaRecorderFragment fragment = new MediaRecorderFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MEDIA_RECORDER_CONFIG_KEY, mediaRecorderConfig);
        fragment.setOnMediaRecorderListener(onMediaRecorderListener);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 设置视频录制的监听
     *
     * @param onMediaRecorderListener
     * @return
     */
    public MediaRecorderFragment setOnMediaRecorderListener(OnMediaRecorderListener onMediaRecorderListener) {
        mOnMediaRecorderListener = onMediaRecorderListener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateMediaRecorder(getActivity());
    }

    /**
     * 处理Activity【防止锁屏和fragment里面放surfaceview，第一次黑屏的问题】
     *
     * @param activity
     */
    public static void onCreateMediaRecorder(Activity activity) {
        if (activity != null) {
            // 防止锁屏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //为了解决fragment里面放surfaceview，第一次黑屏的问题
            activity.getWindow().setFormat(PixelFormat.TRANSLUCENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xvideo_layout_media_recorder, container, false);
        initArgs();
        initViews(view);
        return view;
    }

    private void initArgs() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            MediaRecorderConfig mediaRecorderConfig = bundle.getParcelable(MEDIA_RECORDER_CONFIG_KEY);
            if (mediaRecorderConfig == null) {
                return;
            }
            needFullScreen = mediaRecorderConfig.getFullScreen();
            recordTimeMax = mediaRecorderConfig.getRecordTimeMax();
            recordTimeMin = mediaRecorderConfig.getRecordTimeMin();
            MediaRecorderBase.MAX_FRAME_RATE = mediaRecorderConfig.getMaxFrameRate();
            MediaRecorderBase.NEED_FULL_SCREEN = needFullScreen;
            MediaRecorderBase.MIN_FRAME_RATE = mediaRecorderConfig.getMinFrameRate();
            MediaRecorderBase.SMALL_VIDEO_HEIGHT = mediaRecorderConfig.getSmallVideoHeight();
            MediaRecorderBase.SMALL_VIDEO_WIDTH = mediaRecorderConfig.getSmallVideoWidth();
            MediaRecorderBase.mVideoBitrate = mediaRecorderConfig.getVideoBitrate();
            MediaRecorderBase.CAPTURE_THUMBNAILS_TIME = mediaRecorderConfig.getCaptureThumbnailsTime();
        }
    }

    /**
     * 加载视图
     */
    private void initViews(View view) {
        // ~~~ 绑定控件
        mSurfaceView = view.findViewById(R.id.record_preview);
        mTitleLayout = view.findViewById(R.id.title_layout);
        mCameraSwitch = view.findViewById(R.id.record_camera_switcher);
        mTitleNext = view.findViewById(R.id.title_next);
        mProgressView = view.findViewById(R.id.record_progress);
        mRecordDelete = view.findViewById(R.id.record_delete);
        mRecordController = view.findViewById(R.id.record_controller);
        mBottomLayout = view.findViewById(R.id.bottom_layout);
        mRecordLed = view.findViewById(R.id.record_camera_led);

        mTitleNext.setOnClickListener(this);
        view.findViewById(R.id.title_back).setOnClickListener(this);
        mRecordController.setOnTouchListener(mOnVideoControllerTouchListener);

        // 是否支持前置摄像头
        if (MediaRecorderBase.isSupportFrontCamera()) {
            mCameraSwitch.setOnClickListener(this);
        } else {
            mCameraSwitch.setVisibility(View.GONE);
        }
        // 是否支持闪光灯
        if (DeviceUtils.isSupportCameraLedFlash(getContext().getPackageManager())) {
            mRecordLed.setOnClickListener(this);
        } else {
            mRecordLed.setVisibility(View.GONE);
        }

        mProgressView.setMaxDuration(recordTimeMax);
        mProgressView.setMinTime(recordTimeMin);
    }

    /**
     * 初始化画布
     */
    private void initSurfaceView() {
        if (needFullScreen) {
            mBottomLayout.setBackgroundColor(0);
            mTitleLayout.setBackgroundColor(getResources().getColor(R.color.xvideo_full_title_color));
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSurfaceView
                    .getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            mSurfaceView.setLayoutParams(lp);
            mProgressView.setBackgroundColor(getResources().getColor(R.color.xvideo_full_progress_color));
        } else {
            final int w = DeviceUtils.getScreenWidth(getContext());
            ((RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams()).topMargin = (int) (w / (MediaRecorderBase.SMALL_VIDEO_HEIGHT / (MediaRecorderBase.SMALL_VIDEO_WIDTH * 1.0f)));
            int width = w;
            int height = (int) (w * ((MediaRecorderBase.mSupportedPreviewWidth * 1.0f) / MediaRecorderBase.SMALL_VIDEO_HEIGHT));
            //
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSurfaceView
                    .getLayoutParams();
            lp.width = width;
            lp.height = height;
            mSurfaceView.setLayoutParams(lp);
        }
    }

    /**
     * 初始化拍摄SDK
     */
    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderNative();

        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnEncodeListener(this);
        mMediaRecorder.setOnPreparedListener(this);

        File f = new File(XVideo.getVideoCachePath());
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(key,
                XVideo.getVideoCachePath() + key);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();
    }


    /**
     * 点击屏幕录制
     */
    private View.OnTouchListener mOnVideoControllerTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMediaRecorder == null || mMediaObject == null) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 检测是否手动对焦
                    // 判断是否已经超时
                    if (mMediaObject.getDuration() >= recordTimeMax) {
                        return true;
                    }

                    // 取消回删
                    if (cancelDelete())
                        return true;
                    if (!startState) {
                        startState = true;
                        startRecord();
                    } else {
                        mMediaObject.buildMediaPart(mMediaRecorder.mCameraId);
                        mProgressView.setData(mMediaObject);
                        setStartUI();
                        mMediaRecorder.setRecordState(true);
                    }

                    break;

                case MotionEvent.ACTION_UP:

                    mMediaRecorder.setRecordState(false);
                    if (mMediaObject.getDuration() >= recordTimeMax) {
                        mTitleNext.performClick();
                    } else {
                        mMediaRecorder.setStopDate();
                        setStopUI();
                    }
                    break;
            }
            return true;
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else { //回到页面
            mRecordLed.setChecked(false);
            mMediaRecorder.prepare();
            mProgressView.setData(mMediaObject);
            if (mMediaRecorder instanceof MediaRecorderNative) {
                ((MediaRecorderNative) mMediaRecorder).activityResume();
            }
        }
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        if (mMediaRecorder != null) {

            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            if (part == null) {
                return;
            }

            mProgressView.setData(mMediaObject);
        }

        setStartUI();
    }

    private void setStartUI() {
        mPressedStatus = true;
//		TODO 开始录制的图标
        mRecordController.animate().scaleX(0.8f).scaleY(0.8f).setDuration(500).start();

        if (mHandler != null) {
            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);

            mHandler.removeMessages(HANDLE_STOP_RECORD);
            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD,
                    recordTimeMax - mMediaObject.getDuration());
        }
        mCameraSwitch.setEnabled(false);
        mRecordLed.setEnabled(false);
    }

    public void onBackPressed() {
        if (mMediaObject != null && mMediaObject.getDuration() > 1) {
            // 未转码
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.xvideo_hint)
                    .setMessage(R.string.xvideo_camera_exit_dialog_message)
                    .setNegativeButton(
                            R.string.xvideo_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    cancelRecord();
                                }

                            })
                    .setPositiveButton(R.string.xvideo_camera_cancel_dialog_no,
                            null).setCancelable(false).show();
            return;
        }

        cancelRecord();
    }

    /**
     * 取消视频录制
     */
    protected void cancelRecord() {
        if (mMediaObject != null) {
            mMediaObject.delete();
        }
        onCancel();
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
        }
        setStopUI();
    }

    private void setStopUI() {
        mPressedStatus = false;
        mRecordController.animate().scaleX(1).scaleY(1).setDuration(500).start();

        mCameraSwitch.setEnabled(true);
        mRecordLed.setEnabled(true);

        mHandler.removeMessages(HANDLE_STOP_RECORD);
        checkStatus();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (mHandler.hasMessages(HANDLE_STOP_RECORD)) {
            mHandler.removeMessages(HANDLE_STOP_RECORD);
        }

        // 处理开启回删后其他点击操作
        if (id != R.id.record_delete) {
            if (mMediaObject != null) {
                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
                if (part != null) {
                    if (part.remove) {
                        part.remove = false;
                        if (mProgressView != null) {
                            mProgressView.invalidate();
                        }
                    }
                }
            }
        }

        if (id == R.id.title_back) {
            onBackPressed();
        } else if (id == R.id.record_camera_switcher) {// 前后摄像头切换
            if (mRecordLed.isChecked()) {
                if (mMediaRecorder != null) {
                    mMediaRecorder.toggleFlashMode();
                }
                mRecordLed.setChecked(false);
            }

            if (mMediaRecorder != null) {
                mMediaRecorder.switchCamera();
            }

            if (mMediaRecorder.isFrontCamera()) {
                mRecordLed.setEnabled(false);
            } else {
                mRecordLed.setEnabled(true);
            }
        } else if (id == R.id.record_camera_led) {// 闪光灯
            // 开启前置摄像头以后不支持开启闪光灯
            if (mMediaRecorder != null) {
                if (mMediaRecorder.isFrontCamera()) {
                    return;
                }
            }

            if (mMediaRecorder != null) {
                mMediaRecorder.toggleFlashMode();
            }
        } else if (id == R.id.title_next) {// 停止录制
            stopRecord();
        } else if (id == R.id.record_delete) {
            // 取消回删
            if (mMediaObject != null) {
                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
                if (part != null) {
                    if (part.remove) {
                        part.remove = false;
                        mMediaObject.removePart(part, true);
                    } else {
                        part.remove = true;
                    }
                }
                if (mProgressView != null)
                    mProgressView.invalidate();

                // 检测按钮状态
                checkStatus();
            }
        }
    }


    /**
     * 取消回删
     */
    private boolean cancelDelete() {
        if (mMediaObject != null) {
            MediaObject.MediaPart part = mMediaObject.getCurrentPart();
            if (part != null && part.remove) {
                part.remove = false;
                if (mProgressView != null) {
                    mProgressView.invalidate();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 检查录制时间，显示/隐藏下一步按钮
     */
    private int checkStatus() {
        int duration = 0;
        if (!getActivity().isFinishing() && mMediaObject != null) {
            duration = mMediaObject.getDuration();
            if (duration < recordTimeMin) {
                if (duration == 0) {
                    mCameraSwitch.setVisibility(View.VISIBLE);
                } else {
                    mCameraSwitch.setVisibility(View.GONE);
                }
                // 视频必须大于3秒
                if (mTitleNext.getVisibility() != View.INVISIBLE)
                    mTitleNext.setVisibility(View.INVISIBLE);
            } else {
                // 下一步
                if (mTitleNext.getVisibility() != View.VISIBLE) {
                    mTitleNext.setVisibility(View.VISIBLE);
                }
            }
        }
        return 0;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_INVALIDATE_PROGRESS:
                    if (mMediaRecorder != null && !getActivity().isFinishing()) {
                        if (mMediaObject != null && mMediaObject.getMedaParts() != null && mMediaObject.getDuration() >= recordTimeMax) {
                            mTitleNext.performClick();
                            return true;
                        }
                        if (mProgressView != null) {
                            mProgressView.invalidate();
                        }
                        if (mPressedStatus) {
                            mHandler.sendEmptyMessageDelayed(0, 30);
                        }
                    }
                    return true;
            }
            return true;
        }
    });

    @Override
    public void onEncodeStart() {
        showProgress("", getString(R.string.xvideo_camera_progress_message));
    }

    @Override
    public void onEncodeProgress(int progress) {
    }

    /**
     * 转码完成
     */
    @Override
    public void onEncodeComplete() {
        hideProgress();
        onRecordSuccess(mMediaObject);
    }

    /**
     * 转码失败 检查sdcard是否可用，检查分块是否存在
     */
    @Override
    public void onEncodeError() {
        hideProgress();
        onRecordFailed(getString(R.string.xvideo_video_transcoding_faild));
    }

    @Override
    public void onVideoError(int what, int extra) {

    }

    @Override
    public void onAudioError(int what, String message) {

    }

    @Override
    public void onPrepared() {
        initSurfaceView();
    }

    protected ProgressDialog mProgressDialog;

    public ProgressDialog showProgress(String title, String message) {
        return showProgress(title, message, -1);
    }

    public ProgressDialog showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0) {
                mProgressDialog = new ProgressDialog(getContext(), theme);
            } else {
                mProgressDialog = new ProgressDialog(getContext());
            }
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!StringUtils.isEmpty(title)) {
            mProgressDialog.setTitle(title);
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
        }
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        if (mMediaRecorder != null) {
            if (mMediaRecorder instanceof MediaRecorderNative) {
                ((MediaRecorderNative) mMediaRecorder).activityStop();
            }
            hideProgress();
            mProgressDialog = null;
        }
        super.onStop();
    }

    private void onCancel() {
        if (mOnMediaRecorderListener != null) {
            mOnMediaRecorderListener.onCancel();
        }
    }

    private void onRecordSuccess(MediaObject mediaObject) {
        if (mOnMediaRecorderListener != null) {
            mOnMediaRecorderListener.onRecordSuccess(mediaObject);
        }
    }

    private void onRecordFailed(String msg) {
        if (mOnMediaRecorderListener != null) {
            mOnMediaRecorderListener.onRecordFailed(msg);
        }
    }

    /**
     * 视频录制的监听
     */
    public interface OnMediaRecorderListener {

        /**
         * 取消视频录制
         */
        void onCancel();

        /**
         * 视频录制成功
         *
         * @param mediaObject 录制视频的信息
         */
        void onRecordSuccess(MediaObject mediaObject);

        /**
         * 视频录制失败
         *
         * @param msg 失败原因
         */
        void onRecordFailed(String msg);

    }


}
