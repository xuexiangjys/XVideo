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
 * 视频压缩配置
 *
 * @author xuexiang
 * @since 2018/5/30 下午8:25
 */
public class MediaCompressConfig implements Parcelable {
    /**
     * 码率模式{@link MODE}
     */
    protected int mode = -1;
    /**
     * 固定码率值
     */
    protected int bitrate = -1;
    /**
     * 最大码率值
     */
    protected int maxBitrate = -1;

    protected int bufSize = -1;
    /**
     * 码率等级0~51，越大
     */
    protected int crfSize = -1;
    /**
     * {@link Velocity}  转码速度控制
     */
    protected String velocity;

    protected MediaCompressConfig() {

    }


    protected MediaCompressConfig(Parcel in) {
        mode = in.readInt();
        bitrate = in.readInt();
        maxBitrate = in.readInt();
        bufSize = in.readInt();
        crfSize = in.readInt();
        velocity = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mode);
        dest.writeInt(bitrate);
        dest.writeInt(maxBitrate);
        dest.writeInt(bufSize);
        dest.writeInt(crfSize);
        dest.writeString(velocity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaCompressConfig> CREATOR = new Creator<MediaCompressConfig>() {
        @Override
        public MediaCompressConfig createFromParcel(Parcel in) {
            return new MediaCompressConfig(in);
        }

        @Override
        public MediaCompressConfig[] newArray(int size) {
            return new MediaCompressConfig[size];
        }
    };

    public int getBitrate() {
        return bitrate;
    }

    public int getMaxBitrate() {
        return maxBitrate;
    }

    public int getMode() {
        return mode;
    }

    public int getBufSize() {
        return bufSize;
    }

    public int getCrfSize() {
        return crfSize;
    }

    public String getVelocity() {
        return velocity;
    }

    /**
     * @param velocity 转码速度控制,速度越快体积将变大，质量也稍差一点点 {@link Velocity}
     * @return
     */
    public MediaCompressConfig setVelocity(String velocity) {
        this.velocity = velocity;
        return this;
    }

    public static class MODE {
        /**
         * 默认模式
         */
        public final static int AUTO_VBR = 3;
        /**
         * 这个模式下可设置额定码率
         */
        public final static int VBR = 1;
        /**
         * 固定码率
         */
        public final static int CBR = 2;
    }

    public static class Velocity {
        public final static String ULTRAFAST = "ultrafast";
        public final static String SUPERFAST = "superfast";
        public final static String VERYFAST = "veryfast";
        public final static String FASTER = "faster";
        public final static String FAST = "fast";
        public final static String MEDIUM = "medium";
        public final static String SLOW = "slow";
        public final static String SLOWER = "slower";
        public final static String VERYSLOW = "veryslow";
        public final static String PLACEBO = "placebo";
    }
}
