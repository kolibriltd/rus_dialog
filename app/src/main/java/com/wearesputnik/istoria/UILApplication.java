package com.wearesputnik.istoria;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wearesputnik.istoria.helpers.HttpConnectClass;

public class UILApplication extends Application {
    private static UILApplication mInstance;
    public static HttpConnectClass restInstance = null;
    public static String AppKey;

    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(this);
        initImageLoader(getApplicationContext());

        mInstance = this;
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        ImageLoader.getInstance().init(config.build());
    }

    public static synchronized UILApplication getInstance() {
        return mInstance;
    }
}
