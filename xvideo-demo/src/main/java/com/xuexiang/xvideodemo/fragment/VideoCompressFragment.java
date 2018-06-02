package com.xuexiang.xvideodemo.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.app.IntentUtils;
import com.xuexiang.xutil.app.PathUtils;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.file.FileUtils;
import com.xuexiang.xutil.system.AppExecutors;
import com.xuexiang.xutil.tip.ToastUtils;
import com.xuexiang.xvideo.LocalMediaCompress;
import com.xuexiang.xvideo.MediaRecorderActivity;
import com.xuexiang.xvideo.model.AutoVBRMode;
import com.xuexiang.xvideo.model.BaseMediaBitrateConfig;
import com.xuexiang.xvideo.model.CBRMode;
import com.xuexiang.xvideo.model.LocalMediaConfig;
import com.xuexiang.xvideo.model.OnlyCompressOverBean;
import com.xuexiang.xvideo.model.VBRMode;
import com.xuexiang.xvideodemo.R;
import com.xuexiang.xvideodemo.activity.SendSmallVideoActivity;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 视频压缩
 *
 * @author xuexiang
 * @since 2018/6/2 下午11:42
 */
@Page(name = "视频压缩")
public class VideoCompressFragment extends XPageFragment {
    /**
     * 选择视频
     */
    private static final int REQUEST_CODE_CHOOSE_VIDEO = 200;

    @BindView(R.id.rg_mode)
    RadioGroup rgMode;
    @BindView(R.id.et_crfSize)
    EditText etCrfSize;
    @BindView(R.id.ll_crf)
    LinearLayout llCrf;
    @BindView(R.id.tv_max_bitrate)
    TextView tvMaxBitrate;
    @BindView(R.id.et_max_bitrate)
    EditText etMaxBitrate;
    @BindView(R.id.et_bitrate)
    EditText etBitrate;
    @BindView(R.id.ll_bit_rate)
    LinearLayout llBitRate;
    @BindView(R.id.et_frame_rate)
    EditText etFrameRate;
    @BindView(R.id.et_scale)
    EditText etScale;
    @BindView(R.id.spinner_compress_speed)
    Spinner spinnerCompressSpeed;

    private ProgressDialog mProgressDialog;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_compress;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListeners() {
        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_auto:
                        llCrf.setVisibility(View.VISIBLE);
                        llBitRate.setVisibility(View.GONE);
                        break;
                    case R.id.rb_vbr:
                        llCrf.setVisibility(View.GONE);
                        llBitRate.setVisibility(View.VISIBLE);
                        tvMaxBitrate.setVisibility(View.VISIBLE);
                        etMaxBitrate.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_cbr:
                        llCrf.setVisibility(View.GONE);
                        llBitRate.setVisibility(View.VISIBLE);
                        tvMaxBitrate.setVisibility(View.GONE);
                        etMaxBitrate.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    @OnClick(R.id.btn_choose)
    public void onViewClicked() {
        startActivityForResult(IntentUtils.getDocumentPickerIntent(IntentUtils.DocumentType.VIDEO), REQUEST_CODE_CHOOSE_VIDEO);
    }


    @Override
    @SuppressLint("MissingPermission")
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_VIDEO) {
            String path = PathUtils.getFilePathByUri(intent.getData());
            if (!StringUtils.isEmpty(path)) {

                BaseMediaBitrateConfig compressMode;

                int compressModeCheckedId = rgMode.getCheckedRadioButtonId();

                if (compressModeCheckedId == R.id.rb_cbr) {
                    String bitrate = etBitrate.getText().toString();
                    if (checkStrEmpty(bitrate, "请输入压缩额定码率")) {
                        return;
                    }
                    compressMode = new CBRMode(166, Integer.valueOf(bitrate));
                } else if (compressModeCheckedId == R.id.rb_auto) {
                    String crfSize = etCrfSize.getText().toString();
                    if (TextUtils.isEmpty(crfSize)) {
                        compressMode = new AutoVBRMode();
                    } else {
                        compressMode = new AutoVBRMode(Integer.valueOf(crfSize));
                    }
                } else if (compressModeCheckedId == R.id.rb_vbr) {
                    String maxBitrate = etMaxBitrate.getText().toString();
                    String bitrate = etBitrate.getText().toString();

                    if (checkStrEmpty(maxBitrate, "请输入压缩最大码率") || checkStrEmpty(bitrate, "请输入压缩额定码率")) {
                        return;
                    }
                    compressMode = new VBRMode(Integer.valueOf(maxBitrate), Integer.valueOf(bitrate));
                } else {
                    compressMode = new AutoVBRMode();
                }

                if (!spinnerCompressSpeed.getSelectedItem().toString().equals("none")) {
                    compressMode.setVelocity(spinnerCompressSpeed.getSelectedItem().toString());
                }

                String sRate = etFrameRate.getText().toString();
                String scale = etScale.getText().toString();
                int iRate = 0;
                float fScale = 0;
                if (!TextUtils.isEmpty(sRate)) {
                    iRate = Integer.valueOf(sRate);
                }
                if (!TextUtils.isEmpty(scale)) {
                    fScale = Float.valueOf(scale);
                }
                LocalMediaConfig.Builder builder = new LocalMediaConfig.Builder();
                final LocalMediaConfig config = builder
                        .setVideoPath(path)
                        .captureThumbnailsTime(1)
                        .doH264Compress(compressMode)
                        .setFramerate(iRate)
                        .setScale(fScale)
                        .build();
                showProgress("", "压缩中...", -1);
                AppExecutors.get().singleIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        startCompressVideo(config);
                    }
                });
            } else {
                ToastUtils.toast("获取视频地址失败！");
            }

        }
    }

    private void startCompressVideo(LocalMediaConfig config) {
        OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
        hideProgress();
        Intent intent = new Intent(getContext(), SendSmallVideoActivity.class);
        intent.putExtra(MediaRecorderActivity.OUTPUT_DIRECTORY, FileUtils.getFileByPath(onlyCompressOverBean.getVideoPath()).getParentFile().getPath());
        intent.putExtra(MediaRecorderActivity.VIDEO_URI, onlyCompressOverBean.getVideoPath());
        intent.putExtra(MediaRecorderActivity.VIDEO_SCREENSHOT, onlyCompressOverBean.getPicPath());
        startActivity(intent);
    }

    @MainThread
    private void showProgress(String title, String message, int theme) {
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
    }

    @MainThread
    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private boolean checkStrEmpty(String str, String display) {
        if (TextUtils.isEmpty(str)) {
            ToastUtils.toast(display);
            return true;
        }
        return false;
    }
}
