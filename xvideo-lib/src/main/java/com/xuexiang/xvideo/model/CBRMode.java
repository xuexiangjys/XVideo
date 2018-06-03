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

/**
 * @author xuexiang
 * @since 2018/5/30 下午8:25
 */
public class CBRMode extends MediaCompressConfig {

    /**
     * @param bufSize
     * @param bitrate 固定码率值
     */
    public CBRMode(int bufSize, int bitrate) {
        if (bufSize <= 0 || bitrate <= 0) {
            throw new IllegalArgumentException("bufSize or bitrate value error!");
        }
        this.bufSize = bufSize;
        this.bitrate = bitrate;
        this.mode = MODE.CBR;
    }
}
