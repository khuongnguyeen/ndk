package io.sad.monster.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import io.sad.monster.dialog.AppPurchase;
import io.sad.monster.dialog.PrepareLoadingAdsDialog;
import io.sad.monster.dialog.ResumeLoadingDialog;
import io.sad.monster.util.FirebaseAnalyticsUtil;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String TAG = "AppOpenManager";
    @SuppressLint("StaticFieldLeak")
    private static volatile AppOpenManager INSTANCE;
    private static boolean isShowingAd = false;
    private final List<Class> disabledAppOpenList;
    private Dialog dialog = null;
    private AppOpenAd appResumeAd = null;
    public AppOpenAd splashAd = null;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private String appResumeAdId;
    private String splashAdId;
    private Activity currentActivity;
    private Application myApplication;
    private long appResumeLoadTime = 0;
    private long splashLoadTime = 0;
    private boolean isInitialized = false;// on  - off ad resume on app
    private boolean isAppResumeEnabled = true;
    private Class splashActivity;
    private boolean isLoadResumeAds = false;
    private AppOpenManager() {
        disabledAppOpenList = new ArrayList<>();
    }

    public static synchronized AppOpenManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppOpenManager();
        }
        return INSTANCE;
    }

    public void init(Application application, String appOpenAdId) {
        isInitialized = true;
        this.myApplication = application;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        this.appResumeAdId = appOpenAdId;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public boolean isShowingAd() {
        return isShowingAd;
    }

    public void disableAppResumeWithActivity(Class activityClass) {
        disabledAppOpenList.add(activityClass);
    }

    public void enableAppResumeWithActivity(Class activityClass) {
        disabledAppOpenList.remove(activityClass);
    }

    public void disableAppResume() {
        isAppResumeEnabled = false;
    }

    public void enableAppResume() {
        isAppResumeEnabled = true;
    }

    public void setSplashActivity(Class splashActivity, String adId) {
        this.splashActivity = splashActivity;
        splashAdId = adId;
    }


    public void fetchResumeAds() {
        if (appResumeAd != null) return;

        if (isLoadResumeAds) {
            return;
        }
        if (isAdAvailable(false)) {
            return;
        }

        loadCallback =
            new AppOpenAd.AppOpenAdLoadCallback() {

                @Override
                public void onAdLoaded(AppOpenAd ad) {
                    AppOpenManager.this.appResumeAd = ad;
                    AppOpenManager.this.appResumeLoadTime = (new Date()).getTime();
                    isLoadResumeAds = false;
                    ad.setOnPaidEventListener(adValue -> {
                        FirebaseAnalyticsUtil.logPaidAdImpression(myApplication.getApplicationContext(),
                                adValue,
                                ad.getAdUnitId(),
                                ad.getResponseInfo()
                                        .getMediationAdapterClassName()+"_open_ads_resume");
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    isLoadResumeAds = false;
                }
            };

        if (currentActivity != null) {
            if (AppPurchase.getInstance().isPurchased(currentActivity))
                return;
        }

        AdRequest request = getAdRequest();
        isLoadResumeAds = true;
        AppOpenAd.load(myApplication, appResumeAdId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long loadTime, long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        final long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable(boolean isSplash) {
        long loadTime = isSplash ? splashLoadTime : appResumeLoadTime;
        boolean wasLoadTimeLessThanNHoursAgo = wasLoadTimeLessThanNHoursAgo(loadTime, 4);
        Log.e(TAG, "isAdAvailable: " + wasLoadTimeLessThanNHoursAgo);
        return (isSplash ? splashAd != null : appResumeAd != null)
            && wasLoadTimeLessThanNHoursAgo;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
        if (appResumeAd != null) return;
            if (splashActivity == null) {
                if (!activity.getClass().getName().equals(AdActivity.class.getName())) {
                    fetchResumeAds();
                }
            } else {
                if (!activity.getClass().getName().equals(splashActivity.getName()) && !activity.getClass().getName().equals(AdActivity.class.getName())) {
                    fetchResumeAds();
                }
            }
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }

    public void showAdIfAvailable(final boolean isSplash) {
        if (currentActivity == null || AppPurchase.getInstance().isPurchased(currentActivity))
            return;
        if (!ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED))
            return;
        if (isShowingAd) return;
        if (isAdAvailable(isSplash))
            showResumeAds();
        else if (!isSplash)
                fetchResumeAds();
    }

    public void showSplashAds(SplashAdsShowCallback callback) {
        if (currentActivity == null) return;
        if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            Dialog dialog = null;
            try {
                dialog = new PrepareLoadingAdsDialog(currentActivity);
                dialog.show();
            } catch (Exception e) {
                callback.onShowSuccess();
                e.printStackTrace();
                return;

            }
            final Dialog finalDialog = dialog;
            new Handler().postDelayed(() -> {
                if (splashAd != null) {
                    splashAd.setFullScreenContentCallback(
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                isShowingAd = false;
                                if (callback != null) {
                                    callback.onShowSuccess();
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                if (callback != null) {
                                    callback.onShowSuccess();
                                }
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                isShowingAd = true;
                                splashAd = null;
                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                FirebaseAnalyticsUtil.logClickAdsEvent(currentActivity, splashAdId);
                                Log.e("AppOpenManager", "logClickAdsEvent()   " + currentActivity + ",  " + splashAdId);
                            }
                        });
                    splashAd.show(currentActivity);
                }

                if (currentActivity != null && !currentActivity.isDestroyed() && finalDialog.isShowing()) {
                   try{
                       finalDialog.dismiss();
                   }catch (Exception ex){
                       ex.printStackTrace();
                   }
                }
            }, 800);
        }
    }

    private void showResumeAds() {
        if (appResumeAd == null || currentActivity == null || AppPurchase.getInstance().isPurchased(currentActivity)) {
            return;
        }

        if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {

            try {
                dismissDialogLoading();
                dialog = new ResumeLoadingDialog(currentActivity);
                dialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
            final Dialog finalDialog = dialog;
            new Handler().postDelayed(() -> {
                if (appResumeAd != null) {
                    appResumeAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            appResumeAd = null;
                            isShowingAd = false;

                            if (currentActivity != null && !currentActivity.isDestroyed() && finalDialog != null && finalDialog.isShowing()) {
                                Log.e(TAG, "dismiss dialog loading ad open: ");
                                try {
                                    finalDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            if (currentActivity != null && !currentActivity.isDestroyed() && finalDialog != null && finalDialog.isShowing()) {
                                Log.e(TAG, "dismiss dialog loading ad open: ");
                                try {
                                    finalDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            appResumeAd = null;
                            isShowingAd = false;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                            appResumeAd = null;
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            if (currentActivity != null) {
                                Log.e("AppOpenManager", "logClickAdsEvent()   " + currentActivity + ",  " + splashAdId);
                                FirebaseAnalyticsUtil.logClickAdsEvent(currentActivity, splashAdId);
                            }
                        }
                    });
                    appResumeAd.show(currentActivity);
                }
            }, 800);
        }
    }

    public void fetchSplashAds(SplashAdsLoadCallback callback) {
        loadCallback =
            new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                    Log.e(TAG, "onAppOpenAdLoaded: splash");
                    AppOpenManager.this.splashAd = appOpenAd;
                    splashLoadTime = new Date().getTime();
                    if (callback != null) {
                        callback.onLoadAdsSuccess();
                    }

                    appOpenAd.setOnPaidEventListener(adValue -> {
                        FirebaseAnalyticsUtil.logPaidAdImpression(myApplication.getApplicationContext(),
                                adValue,
                                appOpenAd.getAdUnitId(),
                                appOpenAd.getResponseInfo()
                                        .getMediationAdapterClassName()+"_open_ads_splash");
                    });

                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e(TAG, "onAppOpenAdFailedToLoad: splash " + loadAdError.getMessage());
                    if (callback != null) {
                        callback.onLoadAdsFail();
                    }
                }

            };
        AdRequest request = getAdRequest();
        AppOpenAd.load(myApplication, splashAdId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onResume() {
        if (currentActivity == null) return;

        if (!isAppResumeEnabled) {
            Log.e(TAG, "onResume: app resume is disabled");
            return;
        }
        for (Class activity : disabledAppOpenList) {
            if (activity.getName().equals(currentActivity.getClass().getName())) {
                Log.e(TAG, "onStart: activity is disabled");
                return;
            }
        }
        if (appResumeAd == null) {
                fetchResumeAds();
        } else
            showAdIfAvailable(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Log.e(TAG, "onStop: app stop");
    }

    private void dismissDialogLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // --
    public interface SplashAdsLoadCallback {
        void onLoadAdsSuccess();

        void onLoadAdsFail();
    }

    public interface SplashAdsShowCallback {
        void onShowSuccess();
    }
}

