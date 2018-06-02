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

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuexiang.xutil.app.PathUtils;
import com.xuexiang.xutil.app.SocialShareUtils;
import com.xuexiang.xutil.file.FileUtils;
import com.xuexiang.xvideo.MediaRecorderActivity;
import com.xuexiang.xvideodemo.R;

/**
 * 发送小视频界面
 *
 * @author xuexiang
 * @since 2018/6/2 下午11:19
 */
public class SendSmallVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private String videoUri;
    private TextView tv_send;
    private TextView tv_cancel;
    private String videoScreenshot;
    private ImageView iv_video_screenshot;
    private EditText et_send_content;

    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        tv_cancel.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        et_send_content.setOnClickListener(this);
        iv_video_screenshot.setOnClickListener(this);
    }


    private void initData() {
        Intent intent = getIntent();
        videoUri = intent.getStringExtra(MediaRecorderActivity.VIDEO_URI);
        videoScreenshot = intent.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT);
        Bitmap bitmap = BitmapFactory.decodeFile(videoScreenshot);
        iv_video_screenshot.setImageBitmap(bitmap);
        et_send_content.setHint("您视频地址为:" + videoUri);
    }

    private void initView() {
        setContentView(R.layout.smallvideo_text_edit_activity);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_send = findViewById(R.id.tv_send);
        et_send_content = findViewById(R.id.et_send_content);
        iv_video_screenshot = findViewById(R.id.iv_video_screenshot);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                hesitate();
                break;
            case R.id.tv_send:
                SocialShareUtils.shareVideo(PathUtils.getMediaContentUri(FileUtils.getFileByPath(videoUri)), et_send_content.getText().toString());
                break;
            case R.id.iv_video_screenshot:
                startActivity(new Intent(this, VideoPlayerActivity.class).putExtra(
                        "path", videoUri));
                break;
        }
    }


    @Override
    public void onBackPressed() {
        hesitate();
    }

    private void hesitate() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.xvideo_hint)
                    .setMessage(R.string.xvideo_camera_exit_dialog_message)
                    .setNegativeButton(
                            R.string.xvideo_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    FileUtils.deleteDir(getIntent().getStringExtra(MediaRecorderActivity.OUTPUT_DIRECTORY));

                                    finish();
                                }

                            })
                    .setPositiveButton(R.string.xvideo_camera_cancel_dialog_no,
                            null).setCancelable(false).show();
        } else {
            dialog.show();
        }
    }

}
