package com.xuexiang.xvideodemo.fragment;

import android.hardware.Camera;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.consts.PermissionConsts;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.tip.ToastUtils;
import com.xuexiang.xvideo.XVideo;
import com.xuexiang.xvideo.model.AutoVBRMode;
import com.xuexiang.xvideo.model.MediaCompressConfig;
import com.xuexiang.xvideo.model.MediaRecorderConfig;
import com.xuexiang.xvideodemo.R;
import com.xuexiang.xvideodemo.activity.SendSmallVideoActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author xuexiang
 * @since 2018/6/2 下午9:49
 */
@Page(name = "小视频拍摄高级使用")
public class ComplexUseFragment extends XPageFragment {
    @BindView(R.id.spinner_need_full)
    Spinner spinnerNeedFull;
    @BindView(R.id.tv_size)
    TextView tvSize;
    @BindView(R.id.et_width)
    EditText etWidth;
    @BindView(R.id.et_height)
    EditText etHeight;
    @BindView(R.id.et_max_frame_rate)
    EditText etMaxFrameRate;
    @BindView(R.id.et_record_bitrate)
    EditText etRecordBitrate;
    @BindView(R.id.et_max_time)
    EditText etMaxTime;
    @BindView(R.id.et_min_time)
    EditText etMinTime;
    @BindView(R.id.spinner_record)
    Spinner spinnerRecord;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_complex_use;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        getSupportCameraSize();
    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListeners() {
        spinnerNeedFull.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ("false".equals(((TextView) view).getText().toString())) {
                    etWidth.setVisibility(View.VISIBLE);
                } else {
                    etWidth.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSupportCameraSize() {
        StringBuilder str = new StringBuilder();
        Camera back = null;
        try {
            back = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (back != null) {
            List<Camera.Size> backSizeList = back.getParameters().getSupportedPreviewSizes();
            str.append("经过检查您的摄像头，如使用后置摄像头您可以输入的高度有：");
            for (Camera.Size bSize : backSizeList) {
                str.append(bSize.height).append("、");
            }
            back.release();
        }
        Camera front = null;
        try {
            front = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (front != null) {
            List<Camera.Size> frontSizeList = front.getParameters().getSupportedPreviewSizes();
            str.append("如使用前置摄像头您可以输入的高度有：");
            for (Camera.Size fSize : frontSizeList) {
                str.append(fSize.height).append("、");
            }
            front.release();
        }
        tvSize.setText(str);
    }

    @OnClick(R.id.bt_start)
    public void onViewClicked() {
        startVideoRecorder();
    }

    @Permission({PermissionConsts.CAMERA, PermissionConsts.STORAGE})
    public void startVideoRecorder() {
        String width = etWidth.getText().toString();
        String height = etHeight.getText().toString();
        String maxFrameRate = etMaxFrameRate.getText().toString();
        String bitrate = etRecordBitrate.getText().toString();
        String maxTime = etMaxTime.getText().toString();
        String minTime = etMinTime.getText().toString();
        String s = spinnerNeedFull.getSelectedItem().toString();
        boolean needFull = Boolean.parseBoolean(s);

        MediaCompressConfig recordMode;

        recordMode = new AutoVBRMode();

        if (!"none".equals(spinnerRecord.getSelectedItem().toString())) {
            recordMode.setVelocity(spinnerRecord.getSelectedItem().toString());
        }

        if (!needFull && checkStrEmpty(width, "请输入宽度")) {
            return;
        }
        if (
                checkStrEmpty(height, "请输入高度")
                        || checkStrEmpty(maxFrameRate, "请输入最高帧率")
                        || checkStrEmpty(maxTime, "请输入最大录制时间")
                        || checkStrEmpty(minTime, "请输小最大录制时间")
                        || checkStrEmpty(bitrate, "请输入比特率")
        ) {
            return;
        }

        MediaRecorderConfig config = new MediaRecorderConfig.Builder()
                .fullScreen(needFull)  //是否全屏
                .videoWidth(needFull ? 0 : Integer.valueOf(width)) //视频的宽
                .videoHeight(Integer.valueOf(height))  //视频的高
                .recordTimeMax(Integer.valueOf(maxTime)) //最大录制时间
                .recordTimeMin(Integer.valueOf(minTime)) //最小录制时间
                .maxFrameRate(Integer.valueOf(maxFrameRate)) //最大帧率
                .videoBitrate(Integer.valueOf(bitrate)) //视频码率
                .captureThumbnailsTime(1)
                .build();
        XVideo.startVideoRecorder(this, SendSmallVideoActivity.class.getName(), config);

    }

    private boolean checkStrEmpty(String str, String display) {
        if (TextUtils.isEmpty(str)) {
            ToastUtils.toast(display);
            return true;
        }
        return false;
    }
}
