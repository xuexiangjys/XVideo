# XVideo
[![xv][xvsvg]][xv]  [![api][apisvg]][api]

一个能自动进行压缩的小视频录制库

## 关于我

[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)

## 特征

* 支持自定义小视频录制时的视频质量。

* 支持自定义视频录制的界面。

* 支持自定义最大录制时长和最小录制时长。

* 支持自定义属性的视频压缩。

## 演示（请star支持）

![][demo-gif]

### Demo下载

[![Github](https://img.shields.io/badge/downloads-Github-blue.svg)](https://github.com/xuexiangjys/XVideo/blob/master/apk/xvideo_demo.apk?raw=true)

## 添加Gradle依赖

1.在项目根目录的 build.gradle 的 repositories 添加:

```
allprojects {
     repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

2.在主项目的 build.gradle 中增加依赖。

```
dependencies {
    ···
    implementation 'com.github.xuexiangjys:XVideo:1.0.2'
}
```

3.进行视频录制存储目录地址的设置。

```
/**
 * 初始化xvideo的存放路径
 */
public static void initVideo() {
    XVideo.setVideoCachePath(PathUtils.getExtDcimPath() + "/xvideo/");
    // 初始化拍摄
    XVideo.initialize(false, null);
}
```

## 视频录制

1.视频录制需要`CAMERA`权限和`STORAGE`权限。在Android6.0机器上需要动态获取权限，推荐使用[XAOP](https://github.com/xuexiangjys/XAOP)进行权限申请。

2.调用`MediaRecorderActivity.startVideoRecorder`开始视频录制。

```
/**
 * 开始录制视频
 * @param requestCode 请求码
 */
@Permission({PermissionConsts.CAMERA, PermissionConsts.STORAGE})
public void startVideoRecorder(int requestCode) {
    MediaRecorderConfig mediaRecorderConfig = MediaRecorderConfig.newInstance();
    XVideo.startVideoRecorder(this, mediaRecorderConfig, requestCode);
}
```

3.`MediaRecorderConfig`是视频录制的配置对象，可自定义视频的宽、高、时长以及质量等。

```
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
```

## 视频压缩

使用libx264进行视频压缩。由于手机本身CPU处理能力有限的问题，在手机上进行视频压缩的效率并不是很高，大约压缩的时间需要比视频拍摄本身的时长还要长一些。

```
LocalMediaConfig.Builder builder = new LocalMediaConfig.Builder();
final LocalMediaConfig config = builder
        .setVideoPath(path)  //设置需要进行视频压缩的视频路径
        .captureThumbnailsTime(1)
        .doH264Compress(compressMode) //设置视频压缩的模式
        .setFramerate(iRate)  //帧率
        .setScale(fScale) //压缩比例
        .build();
CompressResult compressResult = XVideo.startCompressVideo(config);
```

## 混淆配置

```
-keep class com.xuexiang.xvideo.jniinterface.** { *; }
```

## 特别感谢

https://github.com/mabeijianxi/small-video-record

## 联系方式

[![](https://img.shields.io/badge/点击一键加入QQ交流群-602082750-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=9922861ef85c19f1575aecea0e8680f60d9386080a97ed310c971ae074998887)

![qq交流群](https://ss.im5i.com/2021/06/15/6RMdO.jpg)

![gzh_weixin.jpg](https://ss.im5i.com/2021/06/14/65yoL.jpg)

[xvsvg]: https://img.shields.io/badge/XVideo-v1.0.2-brightgreen.svg
[xv]: https://github.com/xuexiangjys/XVideo
[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14

[demo-gif]: https://z3.ax1x.com/2021/06/15/2HnMcj.gif
