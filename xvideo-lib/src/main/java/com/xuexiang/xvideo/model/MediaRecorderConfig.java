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

package com.xuexiang.xvideo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 视频录制的配置对象
 *
 * @author xuexiang
 * @since 2018/5/30 下午8:26
 */
public final class MediaRecorderConfig implements Parcelable {

    private final boolean FULL_SCREEN;
    /**
     * 录制时间
     */
    private final int RECORD_TIME_MAX;
    /**
     * 最少录制时间
     */
    private final int RECORD_TIME_MIN;

    /**
     * 小视频高度,TODO 注意：宽度不能随意穿，需要传送手机摄像头手支持录制的视频高度，注意是高度（因为会选择，具体原因不多解析）。
     * 获取摄像头所支持的尺寸的方式是{@link android.graphics.Camera #getSupportedPreviewSizes()}
     * 一般支持的尺寸的高度有：240、480、720、1080等，具体值请用以上方法获取
     */
    private final int SMALL_VIDEO_HEIGHT;
    /**
     * 小视频宽度
     */
    private final int SMALL_VIDEO_WIDTH;
    /**
     * 最大帧率
     */
    private final int MAX_FRAME_RATE;
    /**
     * 最小帧率
     */
    private final int MIN_FRAME_RATE;
    /**
     * 视频码率
     */
    private final int VIDEO_BITRATE;
    /**
     * 录制后会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
     */
    private final int captureThumbnailsTime;

    private final boolean GO_HOME;

    public static MediaRecorderConfig newInstance() {
        return new Builder().build();
    }

    private MediaRecorderConfig(Builder builder) {
        this.FULL_SCREEN = builder.fullscreen;
        this.RECORD_TIME_MAX = builder.recordTimeMax;
        this.RECORD_TIME_MIN = builder.recordTimeMin;
        this.MAX_FRAME_RATE = builder.maxFrameRate;
        this.captureThumbnailsTime = builder.captureThumbnailsTime;
        this.MIN_FRAME_RATE = builder.minFrameRate;
        this.SMALL_VIDEO_HEIGHT = builder.videoHeight;
        this.SMALL_VIDEO_WIDTH = builder.videoWidth;
        this.VIDEO_BITRATE = builder.videoBitrate;
        this.GO_HOME = builder.goHome;
    }

    protected MediaRecorderConfig(Parcel in) {
        FULL_SCREEN = in.readByte() != 0;
        RECORD_TIME_MAX = in.readInt();
        RECORD_TIME_MIN = in.readInt();
        SMALL_VIDEO_HEIGHT = in.readInt();
        SMALL_VIDEO_WIDTH = in.readInt();
        MAX_FRAME_RATE = in.readInt();
        MIN_FRAME_RATE = in.readInt();
        VIDEO_BITRATE = in.readInt();
        captureThumbnailsTime = in.readInt();
        GO_HOME = in.readByte() != 0;
    }

    public static final Creator<MediaRecorderConfig> CREATOR = new Creator<MediaRecorderConfig>() {
        @Override
        public MediaRecorderConfig createFromParcel(Parcel in) {
            return new MediaRecorderConfig(in);
        }

        @Override
        public MediaRecorderConfig[] newArray(int size) {
            return new MediaRecorderConfig[size];
        }
    };

    public boolean isGoHome() {
        return GO_HOME;
    }

    public boolean getFullScreen() {
        return FULL_SCREEN;
    }

    public int getCaptureThumbnailsTime() {
        return captureThumbnailsTime;
    }


    public int getMaxFrameRate() {
        return MAX_FRAME_RATE;
    }

    public int getMinFrameRate() {
        return MIN_FRAME_RATE;
    }

    public int getRecordTimeMax() {
        return RECORD_TIME_MAX;
    }

    public int getRecordTimeMin() {
        return RECORD_TIME_MIN;
    }

    public int getSmallVideoHeight() {
        return SMALL_VIDEO_HEIGHT;
    }

    public int getSmallVideoWidth() {
        return SMALL_VIDEO_WIDTH;
    }

    public int getVideoBitrate() {
        return VIDEO_BITRATE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (FULL_SCREEN ? 1 : 0));
        dest.writeInt(RECORD_TIME_MAX);
        dest.writeInt(RECORD_TIME_MIN);
        dest.writeInt(SMALL_VIDEO_HEIGHT);
        dest.writeInt(SMALL_VIDEO_WIDTH);
        dest.writeInt(MAX_FRAME_RATE);
        dest.writeInt(MIN_FRAME_RATE);
        dest.writeInt(VIDEO_BITRATE);
        dest.writeInt(captureThumbnailsTime);
        dest.writeByte((byte) (GO_HOME ? 1 : 0));
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * 默认录制最长时间
     */
    public static final int DEFAULT_RECORD_TIME_MAX = 6 * 1000;
    /**
     * 默认录制最短时间
     */
    public static final int DEFAULT_RECORD_TIME_MIN = (int) (1.5 * 1000);
    /**
     * 默认小视频高度
     */
    public static final int DEFAULT_VIDEO_HEIGHT = 480;
    /**
     * 默认小视频宽度
     */
    public static final int DEFAULT_VIDEO_WIDTH = 360;
    /**
     * 默认最大帧率
     */
    public static final int DEFAULT_SMALL_MAX_FRAME_RATE = 20;
    /**
     * 默认最小帧率
     */
    public static final int DEFAULT_SMALL_MIN_FRAME_RATE = 8;

    public static class Builder {
        /**
         * 录制时间
         */
        private int recordTimeMax = DEFAULT_RECORD_TIME_MAX;

        private int recordTimeMin = DEFAULT_RECORD_TIME_MIN;

        /**
         * 视频高度,TODO 注意：宽度不能随意穿，需要传送手机摄像头手支持录制的视频高度，注意是高度（因为会选择，具体原因不多解析）。
         * 获取摄像头所支持的尺寸的方式是{@link android.graphics.Camera #getSupportedPreviewSizes()}
         * 一般支持的尺寸的高度有：240、480、720、1080等，具体值请用以上方法获取
         */
        private int videoHeight = DEFAULT_VIDEO_HEIGHT;

        /**
         * 小视频宽度
         */
        private int videoWidth = DEFAULT_VIDEO_WIDTH;
        /**
         * 最大帧率
         */
        private int maxFrameRate = DEFAULT_SMALL_MAX_FRAME_RATE;
        /**
         * 最小帧率
         */
        private int minFrameRate = DEFAULT_SMALL_MIN_FRAME_RATE;
        /**
         * 视频码率//todo 注意传入>0的值后码率模式将从VBR变成CBR
         */
        private int videoBitrate;
        /**
         * 录制后会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
         */
        private int captureThumbnailsTime = 1;

        private boolean goHome = false;

        private boolean fullscreen = false;


        public MediaRecorderConfig build() {
            return new MediaRecorderConfig(this);
        }

        /**
         * @param captureThumbnailsTime 录制后会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
         * @return
         */
        public Builder captureThumbnailsTime(int captureThumbnailsTime) {
            this.captureThumbnailsTime = captureThumbnailsTime;
            return this;
        }

        /**
         * @param maxFrameRate 最大帧率(与视频清晰度、大小息息相关)
         * @return
         */
        public Builder maxFrameRate(int maxFrameRate) {
            this.maxFrameRate = maxFrameRate;
            return this;
        }

        /**
         * @param minFrameRate 最小帧率(与视频清晰度、大小息息相关)
         * @return
         */
        public Builder minFrameRate(int minFrameRate) {
            this.minFrameRate = minFrameRate;
            return this;
        }

        /**
         * @param recordTimeMax 最大录制时间
         * @return
         */
        public Builder recordTimeMax(int recordTimeMax) {
            this.recordTimeMax = recordTimeMax;
            return this;
        }

        /**
         * @param recordTimeMin 最小录制时间
         * @return
         */
        public Builder recordTimeMin(int recordTimeMin) {
            this.recordTimeMin = recordTimeMin;
            return this;
        }


        /**
         * @param videoHeight 小视频高度 ,TODO 注意：宽度不能随意传入，需要传送手机摄像头手支持录制的视频高度，注意是高度（因为会选择，具体原因不多解析）。
         *                    获取摄像头所支持的尺寸的方式是{@link android.graphics.Camera #getSupportedPreviewSizes()}
         *                    一般支持的尺寸的高度有：240、480、720、1080等，具体值请用以上方法获取
         * @return
         */
        public Builder videoHeight(int videoHeight) {
            this.videoHeight = videoHeight;
            return this;
        }

        /**
         * @param videoWidth
         * @return
         */
        public Builder videoWidth(int videoWidth) {
            this.videoWidth = videoWidth;
            return this;
        }

        /**
         * @param videoBitrate 视频码率
         * @return
         */
        public Builder videoBitrate(int videoBitrate) {
            this.videoBitrate = videoBitrate;
            return this;
        }

        public Builder goHome(boolean GO_HOME) {
            this.goHome = GO_HOME;
            return this;
        }

        public Builder fullScreen(boolean full) {
            this.fullscreen = full;
            return this;
        }
    }

}
