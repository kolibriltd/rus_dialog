package com.wearesputnik.istoria;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.onesignal.OneSignal;
import com.wearesputnik.istoria.helpers.Config;
import com.wearesputnik.istoria.helpers.HttpConnectClass;

import org.solovyev.android.checkout.Billing;

public class UILApplication extends Application {
    private static UILApplication mInstance;
    public static HttpConnectClass restInstance = null;
    public static String AppKey;
    private Tracker mTracker;
    private static GoogleAnalytics sAnalytics;


    private final Billing billing = new Billing(this, new Billing.DefaultConfiguration() {
        @Override
        public String getPublicKey() {
            return Config.BASE64_PUBLIC_KEY;
        }
    });

    public Billing getBilling() {
        return billing;
    }

    /*synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // Чтобы включить ведение журнала отладки, используйте adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }*/

   ///* private final Checkout checkout = Checkout.forApplication(billing, Inventory.Products.create().add(IN_APP, asList("product")));

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        billing.connect();

        mInstance = this;
    }

    public static synchronized UILApplication getInstance() {
        return mInstance;
    }


    /*public Checkout getCheckout() {
        return checkout;
    }*/
}
