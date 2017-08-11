package com.wearesputnik.istoria;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wearesputnik.istoria.helpers.HttpConnectClass;

import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Cache;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;

public class UILApplication extends Application {
    private static UILApplication mInstance;
    public static HttpConnectClass restInstance = null;
    public static String AppKey;


    private final Billing billing = new Billing(this, new Billing.DefaultConfiguration() {
        @Override
        public String getPublicKey() {
            return "Your public key, don't forget abput encryption";
        }
    });

    public Billing getBilling() {
        return billing;
    }

   ///* private final Checkout checkout = Checkout.forApplication(billing, Inventory.Products.create().add(IN_APP, asList("product")));

    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(this);
        initImageLoader(getApplicationContext());

        billing.connect();

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


    /*public Checkout getCheckout() {
        return checkout;
    }*/
}
