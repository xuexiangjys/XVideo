/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xvideodemo.activity;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.display.ScreenUtils;
import com.xuexiang.xvideo.MediaRecorderBase;
import com.xuexiang.xvideo.SurfaceVideoView;
import com.xuexiang.xvideodemo.R;

/**
 * 通用单独播放界面
 *
 * @author tangjun
 */
public class VideoPlayerActivity extends AppCompatActivity implements
        SurfaceVideoView.OnPlayStateListener, OnErrorListener,
        OnPreparedListener, OnClickListener, OnCompletionListener,
        OnInfoListener {

    /**
     * 播放控件
     */
    private SurfaceVideoView mVideoView;
    /**
     * 暂停按钮
     */
    private View mPlayerStatus;
    private View mLoading;

    /**
     * 播放路径
     */
    private String mPath;
    /**
     * 是否需要回复播放
     */
    private boolean mNeedResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mPath = getIntent().getStringExtra("path");
        if (StringUtils.isEmpty(mPath)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_video_player);
        mVideoView = findViewById(R.id.video_view);

        int screenWidth = ScreenUtils.getScreenWidth();
        int videoHeight = (int) (screenWidth / (MediaRecorderBase.SMALL_VIDEO_HEIGHT / (MediaRecorderBase.SMALL_VIDEO_WIDTH  * 1.0f)));
        mVideoView.getLayoutParams().height = videoHeight;
        mVideoView.requestLayout();

        mPlayerStatus = findViewById(R.id.play_status);
        mLoading = findViewById(R.id.loading);

        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnPlayStateListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);


        mVideoView.setOnClickListener(this);
        mPlayerStatus.setOnClickListener(this);

        mVideoView.setVideoPath(mPath);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null && mNeedResume) {
            mNeedResume = false;
            if (mVideoView.isRelease()) {
                mVideoView.reOpen();
            } else {
                mVideoView.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                mNeedResume = true;
                mVideoView.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.release();
            mVideoView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoView.setVolume(SurfaceVideoView.getSystemVolumn(this));
        mVideoView.start();
        // new Handler().postDelayed(new Runnable() {
        //
        // @SuppressWarnings("deprecation")
        // @Override
        // public void run() {
        // if (DeviceUtils.hasJellyBean()) {
        // mVideoView.setBackground(null);
        // } else {
        // mVideoView.setBackgroundDrawable(null);
        // }
        // }
        // }, 300);
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {// 跟随系统音量走
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                mVideoView.dispatchKeyEvent(this, event);
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onStateChanged(boolean isPlaying) {
        mPlayerStatus.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (!isFinishing()) {
            // 播放失败
        }
        finish();
        return false;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_view:
            case R.id.play_status:
                mVideoView.reOpen();
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        if (!isFinishing()) {
//            mVideoView.reOpen();
//        }
        mPlayerStatus.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                // 音频和视频数据不正确
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (!isFinishing()) {
                    mVideoView.pause();
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (!isFinishing()) {
                    mVideoView.start();
                }
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mVideoView.setBackground(null);
                } else {
                    mVideoView.setBackground(null);
                }
                break;
        }
        return false;
    }

}
