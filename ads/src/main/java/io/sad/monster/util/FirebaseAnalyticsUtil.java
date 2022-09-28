package io.sad.monster.util;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdValue;
import com.google.firebase.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

public class FirebaseAnalyticsUtil {
    private static final String TAG = "FirebaseAnalyticsUtil";

    public static void logPaidAdImpression(Context context, AdValue adValue, String adUnitId, String mediationAdapterClassName) {
        Log.e(TAG, String.format(
                "Paid event of value %d microcents in currency %s of precision %s%n occurred for ad unit %s from ad network %s.",
                adValue.getValueMicros(),
                adValue.getCurrencyCode(),
                adValue.getPrecisionType(),
                adUnitId,
                mediationAdapterClassName));

        ActivityManager am = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);
        String currentActivity = taskInfo.get(0).topActivity.getClassName();
        if (BuildConfig.DEBUG) Toast.makeText(context, currentActivity, Toast.LENGTH_SHORT).show();

        Bundle params = new Bundle(); // Ghi giá trị quảng cáo ở dạng micro.
        params.putString("activity", currentActivity);
        params.putLong("valuemicros", adValue.getValueMicros());
        params.putString("currency", adValue.getCurrencyCode());
        params.putInt("precision", adValue.getPrecisionType());
        params.putString("adunitid", adUnitId);
        params.putString("network", mediationAdapterClassName);
        logPaidAdImpressionValue(context, adValue.getValueMicros() / 1000000.0, adValue.getPrecisionType(), adUnitId, mediationAdapterClassName);
        FirebaseAnalytics.getInstance(context).logEvent("paid_ad_impression", params);
        SharePreferenceUtils.updateCurrentTotalRevenueAd(context, (float) adValue.getValueMicros());
        logCurrentTotalRevenueAd(context, "event_current_total_revenue_ad");
        logTotalRevenueAdIn3DaysIfNeed(context);
        logTotalRevenueAdIn7DaysIfNeed(context);
    }

    private static void logPaidAdImpressionValue(Context context, double value, int precision, String adunitid, String network) {
        Bundle params = new Bundle(); // Log ad value in micros.
        params.putDouble("value", value);
        params.putString("currency", "USD");
        params.putInt("precision", precision);
        params.putString("adunitid", adunitid);
        params.putString("network", network);
        FirebaseAnalytics.getInstance(context).logEvent("paid_ad_impression_value", params);
    }

    public static void logClickAdsEvent(Context context, String adUnitId) {
        Log.e(TAG, String.format("User click ad for ad unit %s.", adUnitId));
        Bundle bundle = new Bundle();
        bundle.putString("ad_unit_id", adUnitId);
        FirebaseAnalytics.getInstance(context).logEvent("event_user_click_ads", bundle);
    }

    public static void logCurrentTotalRevenueAd(Context context, String eventName) {
        float currentTotalRevenue = SharePreferenceUtils.getCurrentTotalRevenueAd(context);
        Bundle bundle = new Bundle();
        bundle.putFloat("value", currentTotalRevenue);
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle);
    }

    public static void logTotalRevenueAdIn3DaysIfNeed(Context context) {
        long installTime = SharePreferenceUtils.getInstallTime(context);
        if (!SharePreferenceUtils.isPushRevenue3Day(context)
                && (System.currentTimeMillis() - installTime >= 3L * 24 * 60 * 60 * 1000)) {
            Log.e(TAG, "logTotalRevenueAdAt3DaysIfNeed: ");
            logCurrentTotalRevenueAd(context, "event_total_revenue_ad_in_3_days");
            SharePreferenceUtils.setPushedRevenue3Day(context);
        }
    }

    public static void logTotalRevenueAdIn7DaysIfNeed(Context context) {
        long installTime = SharePreferenceUtils.getInstallTime(context);
        if (!SharePreferenceUtils.isPushRevenue7Day(context)
                && (System.currentTimeMillis() - installTime >= 7L * 24 * 60 * 60 * 1000)) {
            logCurrentTotalRevenueAd(context, "event_total_revenue_ad_in_7_days");
            SharePreferenceUtils.setPushedRevenue7Day(context);
        }
    }
}
