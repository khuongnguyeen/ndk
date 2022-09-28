package io.sad.monster.ads.application;

import android.app.Application;

import com.google.firebase.FirebaseApp;

import io.sad.monster.ads.Ads;
import io.sad.monster.ads.AppOpenManager;
import io.sad.monster.util.SharePreferenceUtils;

public abstract class AdsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Ads.getInstance().init(this);

        if (enableAdsResume()) {
            AppOpenManager.getInstance().init(this, getOpenResumeAppAdId());
        }
        if (SharePreferenceUtils.getInstallTime(this) == 0) {
            SharePreferenceUtils.setInstallTime(this);
        }
        FirebaseApp.initializeApp(getApplicationContext());

    }

    public abstract boolean enableAdsResume();

    public abstract String getOpenResumeAppAdId();

}
