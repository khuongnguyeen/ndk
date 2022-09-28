package io.sad.monster.util;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.sad.monster.R;
import io.sad.monster.ads.Ads;
import io.sad.monster.callback.AdCallback;
import io.sad.monster.dialog.AppPurchase;

public class Adz {


    private static final Map<String, Long> mapCacheNative = new HashMap<>();
    public static final Map<String, InterstitialAd> listTypeInter = new HashMap<>();
    public static final Map<String, NativeAd> listTypeNative = new HashMap<>();
    private static final Map<String, Boolean> mapLoading = new LinkedHashMap<String, Boolean>();


    private static long timeReload = 120000;

    public static final String NATIVE_EXIT = "NATIVE_EXIT";

    public static final String LOADING_BIG = "LOADING_BIG";
    public static final String LOADING_NORMAL = "LOADING_NORMAL";
    public static final String LOADING_SMALL = "LOADING_SMALL";

    public static void setTimeReloadNative(long s) {
        timeReload = s;
    }

    public static void loadShimmerNative(Context context, FrameLayout viewLayout, int resource) {
        ShimmerFrameLayout adView = (ShimmerFrameLayout) LayoutInflater.from(context).inflate(resource, null);
        viewLayout.removeAllViews();
        viewLayout.addView(adView);
    }

    public static void showViewAdsHideShimmerNative(Context context, FrameLayout viewLayout, NativeAd unifiedNativeAd, int resource) {
        NativeAdView adView = (NativeAdView) LayoutInflater.from(context).inflate(resource, null);
        viewLayout.removeAllViews();
        viewLayout.addView(adView);
        Ads.getInstance().populateUnifiedNativeAdView(unifiedNativeAd, adView);
    }


    public static NativeAd getNativeAds(String type) {
        if(TextUtils.isEmpty(type)){
            return null;
        }
        if(listTypeNative.size()==0) return null;

        Long time = mapCacheNative.get(type);
        if (!type.equals(NATIVE_EXIT) && ( time == null || (time - Calendar.getInstance().getTime().getTime()) > timeReload)) {
            return null;
        }
        for (Map.Entry<String, NativeAd> entry : listTypeNative.entrySet()) {
            if (entry.getKey().equals(type)) return entry.getValue();
        }
        return null;
    }

    public static void setNativeAds(String type, NativeAd nativeAds) {
        if(listTypeNative.size()==0) return;

        for (Map.Entry<String, NativeAd> entry : listTypeNative.entrySet()) {
            if (entry.getKey().equals(type)) {
                mapCacheNative.put(type, Calendar.getInstance().getTime().getTime());
                entry.setValue(nativeAds);
            }
        }

    }

    public static int getLayoutLoading(String type) {
        switch (type) {
            case LOADING_BIG:
                return R.layout.load_big_native;
            case LOADING_NORMAL:
                return R.layout.load_normal_native;
            case LOADING_SMALL:
                return R.layout.load_fb_banner;
            default:
                return R.layout.load_fb_banner;
        }

    }

    public static int getLayoutNative(String type) {
        switch (type) {
            case LOADING_BIG:
                return R.layout.native_view_custom_big_size;
            case LOADING_NORMAL:
                return R.layout.native_view_custom_normal_size;
            case LOADING_SMALL:
                return R.layout.custom_native;
            default:
                return R.layout.custom_native;
        }

    }

    //InterstitialAd


    public static void setInterstitialAds(InterstitialAd mInter, String type) {
        if (TextUtils.isEmpty(type)) return;
        if(listTypeInter.size()==0) return;
        for (Map.Entry<String, InterstitialAd> entry : listTypeInter.entrySet()) {
            if (entry.getKey().equals(type)) entry.setValue(mInter);
        }

    }



    public static InterstitialAd getInterstitialAds(String type) {
        if (TextUtils.isEmpty(type)) return null;
        if(listTypeInter.size()==0) return null;
        for (Map.Entry<String, InterstitialAd> entry : listTypeInter.entrySet()) {
            if (entry.getKey().equals(type)) return entry.getValue();
        }
        return null;

    }

    public static void clearAds(String type) {
        if (TextUtils.isEmpty(type)) return;
        if(listTypeInter.size()==0) return;
        for (Map.Entry<String, InterstitialAd> entry : listTypeInter.entrySet()) {
            if (entry.getKey().equals(type)) entry.setValue(null);
        }

    }





    public static void loadAdInterstitial(Context context, String id, InterstitialAd interstitialAds, String type) {
        if (AppPurchase.getInstance().isPurchased(context)) return;
        if (interstitialAds != null) return;
        if (mapLoading.containsKey(id)) return;
        mapLoading.put(id, true);
        Ads.getInstance().getInterstitialAds(context, id, new AdCallback() {
            @Override
            public void onInterstitialLoad(@Nullable InterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                setInterstitialAds(interstitialAd, type);
                mapLoading.remove(id);
            }

            @Override
            public void onAdFailedToLoad(@Nullable LoadAdError i) {
                super.onAdFailedToLoad(i);
                mapLoading.remove(id);
            }
        });

    }




}
