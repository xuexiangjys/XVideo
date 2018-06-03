package com.xuexiang.xvideo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.xuexiang.xvideo.model.LocalMediaConfig;
import com.xuexiang.xvideo.model.MediaObject;
import com.xuexiang.xvideo.model.CompressResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 本地视频压缩器
 *
 * @author xuexiang
 * @since 2018/5/30 下午9:17
 */
public class LocalMediaCompress extends MediaRecorderBase {
    private final String mNeedCompressVideo;
    private final CompressResult mCompressResult;
    /**
     * 本地压缩视频的信息
     */
    private final LocalMediaConfig localMediaConfig;
    protected String scaleWH = "";

    @Override
    public MediaObject.MediaPart startRecord() {
        return null;
    }

    public LocalMediaCompress(LocalMediaConfig localMediaConfig) {
        this.localMediaConfig = localMediaConfig;
        compressConfig = localMediaConfig.getCompressConfig();
        CAPTURE_THUMBNAILS_TIME = localMediaConfig.getCaptureThumbnailsTime();
        if (localMediaConfig.getFrameRate() > 0) {
            setTranscodingFrameRate(localMediaConfig.getFrameRate());
        }
        mNeedCompressVideo = localMediaConfig.getVideoPath();
        mCompressResult = new CompressResult();
        mCompressResult.setVideoPath(mNeedCompressVideo);

    }

    private String getScaleWH(String videoPath, float scale) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoPath);
        String s = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        String videoW = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String videoH = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        int srcW = Integer.valueOf(videoW);
        int srcH = Integer.valueOf(videoH);
        int newSrcW = (int) (srcW / scale);
        int newSrcH = (int) (srcH / scale);
        if (newSrcH % 2 != 0) {
            newSrcH += 1;
        }
        if (newSrcW % 2 != 0) {
            newSrcW += 1;
        }
        if (s.equals("90") || s.equals("270")) {
            return String.format("%dx%d", newSrcH, newSrcW);

        } else if (s.equals("0") || s.equals("180") || s.equals("360")) {
            return String.format("%dx%d", newSrcW, newSrcH);
        } else {
            return "";
        }
    }

    private void correctAttribute(String videoPath, String picPath) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoPath);
        String s = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        String videoW = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String videoH = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

        if (s.equals("90") || s.equals("270")) {
            SMALL_VIDEO_WIDTH = Integer.valueOf(videoW);
            SMALL_VIDEO_HEIGHT = Integer.valueOf(videoH);
            String newPicPath = checkPicRotating(Integer.valueOf(s), picPath);
            if (!TextUtils.isEmpty(newPicPath)) {
                mCompressResult.setPicPath(newPicPath);
            }

        } else if (s.equals("0") || s.equals("180") || s.equals("360")) {
            SMALL_VIDEO_HEIGHT = Integer.valueOf(videoW);
            SMALL_VIDEO_WIDTH = Integer.valueOf(videoH);
        }

    }

    @Override
    public String getScaleWH() {
        return scaleWH;
    }

    private String checkPicRotating(int angle, String picPath) {
        Bitmap bitmap = rotatingImageView(angle, BitmapFactory.decodeFile(picPath));
        return savePhoto(bitmap);
    }

    private Bitmap rotatingImageView(int angle, Bitmap bitmap) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    private String savePhoto(Bitmap bitmap) {

        FileOutputStream fileOutputStream = null;

        String fileName = UUID.randomUUID().toString() + ".jpg";
        File f = new File(mMediaObject.getOutputDirectory(), fileName);
        try {
            fileOutputStream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        } finally {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return f.toString();
    }

    public CompressResult startCompress() {

        if (TextUtils.isEmpty(mNeedCompressVideo)) {
            return mCompressResult;
        }

        File f = new File(XVideo.getVideoCachePath());
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = setOutputDirectory(key,
                XVideo.getVideoCachePath() + key);

        mMediaObject.setOutputTempVideoPath(mNeedCompressVideo);

        float scale = localMediaConfig.getScale();
        if (scale > 1) {
            scaleWH = getScaleWH(mNeedCompressVideo, scale);
        }

        boolean b = doCompress(true);
        mCompressResult.setSuccess(b);

        if (b) {
            mCompressResult.setVideoPath(mMediaObject.getOutputTempTranscodingVideoPath());
            mCompressResult.setPicPath(mMediaObject.getOutputVideoThumbPath());
            correctAttribute(mMediaObject.getOutputTempTranscodingVideoPath(), mMediaObject.getOutputVideoThumbPath());
        }

        return mCompressResult;
    }


}
