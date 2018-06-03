package com.xuexiang.xvideodemo.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerController;
import com.xiao.nicevideoplayer.TxVideoPlayerController;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xaop.consts.PermissionConsts;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.app.PathUtils;
import com.xuexiang.xutil.app.SocialShareUtils;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.file.FileUtils;
import com.xuexiang.xvideo.MediaRecorderActivity;
import com.xuexiang.xvideo.MediaRecorderFragment;
import com.xuexiang.xvideo.XVideo;
import com.xuexiang.xvideo.model.MediaRecorderConfig;
import com.xuexiang.xvideodemo.R;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * @author xuexiang
 * @since 2018/6/2 下午6:06
 */
@Page(name = "小视频拍摄简单使用")
public class SimpleUseFragment extends XPageFragment implements TxVideoPlayerController.OnShareListener{

    /**
     * 小视频录制
     */
    private static final int REQUEST_CODE_VIDEO = 100;

    @BindView(R.id.video_player)
    NiceVideoPlayer videoPlayer;
    @BindView(R.id.btn_share)
    Button btnShare;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_simple_use;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        btnShare.setEnabled(false);
    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListeners() {

    }


    /**
     * 开始录制视频
     * @param requestCode 请求码
     */
    @Permission({PermissionConsts.CAMERA, PermissionConsts.STORAGE})
    public void startVideoRecorder(int requestCode) {
        MediaRecorderConfig mediaRecorderConfig = MediaRecorderConfig.newInstance();
        XVideo.startVideoRecorder(this, mediaRecorderConfig, requestCode);
    }

    @SingleClick
    @OnClick({R.id.btn_video, R.id.btn_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_video:
                startVideoRecorder(REQUEST_CODE_VIDEO);
                break;
            case R.id.btn_share:
                SocialShareUtils.shareVideo(PathUtils.getMediaContentUri(FileUtils.getFileByPath(videoPlayer.getUrl())), "小视频分享");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_VIDEO) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String videoUri = bundle.getString(MediaRecorderActivity.VIDEO_URI);
                String videoScreenshot = bundle.getString(MediaRecorderActivity.VIDEO_SCREENSHOT);
                videoPlayer.setUp(videoUri, null);
                videoPlayer.setController(buildPlayerController("这是XVideo拍摄的视频！", videoScreenshot));
                btnShare.setEnabled(true);
            }
        }
    }

    /**
     * 构建播放器的控制器
     *
     * @param title      标题
     * @param screenshot 截图
     * @return
     */
    private NiceVideoPlayerController buildPlayerController(String title, String screenshot) {
        TxVideoPlayerController controller = new TxVideoPlayerController(getContext(), this);
        controller.imageView().setBackgroundColor(Color.BLACK);
        controller.setTitle(title);
        if (!StringUtils.isEmpty(screenshot)) {
            Glide.with(this)
                    .load(screenshot)
                    .placeholder(R.drawable.player_img_default)
                    .crossFade()
                    .into(controller.imageView());
        }
        return controller;
    }

    /**
     * 分享
     */
    @Override
    public void onShare() {
        SocialShareUtils.shareVideo(PathUtils.getMediaContentUri(FileUtils.getFileByPath(videoPlayer.getUrl())), "小视频分享");
    }
}
