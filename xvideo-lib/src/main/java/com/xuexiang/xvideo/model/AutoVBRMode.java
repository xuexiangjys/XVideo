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
 *
 *
 * @author xuexiang
 * @since 2018/5/30 下午8:25
 */
public class AutoVBRMode extends BaseMediaBitrateConfig {

    public AutoVBRMode(){
        this.mode= MODE.AUTO_VBR;
    }

    /**
     *
     * @param crfSize 压缩等级，0~51，值越大约模糊，视频越小，建议18~28.
     */
    public AutoVBRMode(int crfSize){
        if(crfSize<0||crfSize>51){
            throw  new IllegalArgumentException("crfSize 在0~51之间");
        }
        this.crfSize=crfSize;
        this.mode= MODE.AUTO_VBR;
    }
}
