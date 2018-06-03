package com.xuexiang.xvideodemo;

import android.app.Application;
import android.content.Context;

import com.xuexiang.xaop.XAOP;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xpage.AppPageConfig;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.PageConfiguration;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xutil.app.PathUtils;
import com.xuexiang.xvideo.XVideo;

import java.util.List;

/**
 * ================================================
 * <p>
 * 应用的入口，配置基础library的依赖引用配置 <br>
 * Created by XAndroidTemplate <br>
 * <a href="mailto:xuexiangjys@gmail.com">Contact me</a>
 * <a href="https://github.com/xuexiangjys">Follow me</a>
 * </p>
 * ================================================
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLibs();
    }

    private void initLibs() {
        XUtil.init(this);
        XUtil.debug(true);
        XAOP.init(this);

        PageConfig.getInstance().setPageConfiguration(new PageConfiguration() {
            @Override
            public List<PageInfo> registerPages(Context context) {
                return AppPageConfig.getInstance().getPages();
            }
        }).debug("PageLog").enableWatcher(false).init(this);

        initVideo();
    }

    /**
     * 初始化video的存放路径
     */
    public static void initVideo() {
        XVideo.setVideoCachePath(PathUtils.getExtDcimPath() + "/xvideo/");
        // 初始化拍摄
        XVideo.initialize(false, null);
    }
}
