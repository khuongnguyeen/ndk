package io.ads.ndk.ads;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.Objects;

import io.ads.ndk.R;
import io.sad.monster.BuildConfig;
import io.sad.monster.ads.Ads;
import io.sad.monster.callback.AdCallback;
import io.sad.monster.dialog.AppPurchase;
import io.sad.monster.util.Adz;

public class AdsUtil {
    public static final String OPEN_APP_AD_UNIT = "ca-app-pub-3940256099942544/3419835294";
    public static final String NATIVE_AD_UNIT = "ca-app-pub-3940256099942544/2247696110";
    public static final String INTER_AD_UNIT = "ca-app-pub-3940256099942544/1033173712";
    public static Boolean BUILD_DEBUG = BuildConfig.DEBUG;

    public static InterstitialAd interSplash;
    public static InterstitialAd interIntro;
    public static InterstitialAd interstitialAdAllFile;



    public static void showStaticNativeBig(Context context, String id, String typeNative, FrameLayout viewLayout)
    {
        if (Adz.getNativeAds(typeNative) != null) {
            if (BUILD_DEBUG)
                Toast.makeText(context, "Show Native exit ...", Toast.LENGTH_SHORT).show();
            Adz.showViewAdsHideShimmerNative(context, viewLayout, Adz.getNativeAds(typeNative), Adz.getLayoutNative(Adz.LOADING_BIG));

        } else {
            if (!AppPurchase.getInstance().isPurchased(context)) {
                Adz.loadShimmerNative(context, viewLayout, Adz.getLayoutLoading(Adz.LOADING_BIG));
            }
            Ads.getInstance().loadNativeAd(context, id, new AdCallback() {
                @Override
                public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                    super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                    if (BUILD_DEBUG)
                        Toast.makeText(context, "Loading Native success ...", Toast.LENGTH_SHORT).show();
                    Adz.setNativeAds(typeNative, unifiedNativeAd);
                    Adz.showViewAdsHideShimmerNative(context, viewLayout, unifiedNativeAd, Adz.getLayoutNative(Adz.LOADING_BIG));
                }

                @Override
                public void onAdFailedToLoad(@Nullable LoadAdError i) {
                    super.onAdFailedToLoad(i);
                    if (BUILD_DEBUG)
                        Toast.makeText(context, "Loading Native failed ...", Toast.LENGTH_SHORT).show();
                    NativeAd nativeAd = Adz.getNativeAds(typeNative);
                    if (nativeAd == null) {
                        viewLayout.removeAllViews();
                    } else {
                        Adz.showViewAdsHideShimmerNative(context, viewLayout, nativeAd, Adz.getLayoutNative(Adz.LOADING_BIG));
                    }
                }
            });
        }
    }

    public static void showStaticNativeNormal(Context context, String id, String typeNative, FrameLayout viewLayout) {
        if (Adz.getNativeAds(typeNative) != null ) {
            if (BUILD_DEBUG)
                Toast.makeText(context, "Show Native exit ...", Toast.LENGTH_SHORT).show();
            Adz.showViewAdsHideShimmerNative(context, viewLayout, Adz.getNativeAds(typeNative), Adz.getLayoutNative(Adz.LOADING_NORMAL));

        } else {
            if (!AppPurchase.getInstance().isPurchased(context))
                Adz.loadShimmerNative(context, viewLayout, Adz.getLayoutLoading(Adz.LOADING_NORMAL));
            Ads.getInstance().loadNativeAd(context, id, new AdCallback() {
                @Override
                public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                    super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                    if (BUILD_DEBUG)
                        Toast.makeText(context, "Loading Native success ...", Toast.LENGTH_SHORT).show();
                    Adz.setNativeAds(typeNative, unifiedNativeAd);
                    Adz.showViewAdsHideShimmerNative(context, viewLayout, unifiedNativeAd, Adz.getLayoutNative(Adz.LOADING_NORMAL));
                }

                @Override
                public void onAdFailedToLoad(@Nullable LoadAdError i) {
                    super.onAdFailedToLoad(i);
                    if (BUILD_DEBUG)
                        Toast.makeText(context, "Loading Native failed ...", Toast.LENGTH_SHORT).show();
                    NativeAd nativeAd = Adz.getNativeAds(typeNative);
                    if (nativeAd == null) {
                        viewLayout.removeAllViews();
                    } else {
                        Adz.showViewAdsHideShimmerNative(context, viewLayout, nativeAd, Adz.getLayoutNative(Adz.LOADING_NORMAL));
                    }

                }
            });
        }
    }

    public static void showStaticNativeSmall(Context context, String id, String typeNative, FrameLayout viewLayout) {
        if (Adz.getNativeAds(typeNative) != null) {
            if (BUILD_DEBUG)
                Toast.makeText(context, "Show Native exit ...", Toast.LENGTH_SHORT).show();
            Adz.showViewAdsHideShimmerNative(context, viewLayout, Adz.getNativeAds(typeNative), Adz.getLayoutNative(Adz.LOADING_SMALL));

        } else {
            if (!AppPurchase.getInstance().isPurchased(context)) {
                Adz.loadShimmerNative(context, viewLayout, Adz.getLayoutLoading(Adz.LOADING_SMALL));
            }

            Ads.getInstance().loadNativeAd(context, id, new AdCallback() {
                @Override
                public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                    super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                    if (BUILD_DEBUG)
                        Toast.makeText(context, "Loading Native success ...", Toast.LENGTH_SHORT).show();
                    Adz.setNativeAds(typeNative, unifiedNativeAd);
                    Adz.showViewAdsHideShimmerNative(context, viewLayout, unifiedNativeAd, Adz.getLayoutNative(Adz.LOADING_SMALL));
                }

                @Override
                public void onAdFailedToLoad(@Nullable LoadAdError i) {
                    super.onAdFailedToLoad(i);
                    if (BUILD_DEBUG)
                        Toast.makeText(context, "Loading Native failed ...", Toast.LENGTH_SHORT).show();
                    NativeAd nativeAd = Adz.getNativeAds(typeNative);
                    if (nativeAd == null) {
                        viewLayout.removeAllViews();
                    } else {
                        Adz.showViewAdsHideShimmerNative(context, viewLayout, nativeAd, Adz.getLayoutNative(Adz.LOADING_SMALL));
                    }
                }
            });
        }
    }

    public static void loadInterByType(Context context,String type){
        Adz.loadAdInterstitial(context, AdsUtil.getKeyInter(context, type), Adz.getInterstitialAds(type), type);
    }


    public static Pair<String, InterstitialAd> getInterstitialAdsForShow(String type) {
        if (TextUtils.isEmpty(type)) return null;
        InterstitialAd temp = null;
        String typeStrCache = null;
        switch (type) {
            case Constants.INTER_SPLASH: {
                typeStrCache = type;
                temp = interSplash;
                break;
            }
            case Constants.INTER_INTRO: {
                if (interSplash != null) {
                    typeStrCache = Constants.INTER_SPLASH;
                    temp = interSplash;
                } else {
                    typeStrCache = Constants.INTER_INTRO;
                    temp = interIntro;
                }
                break;
            }
            case Constants.INTER_OTHER: {
                if (interstitialAdAllFile == null) {
                    if (interSplash != null) {
                        typeStrCache = Constants.INTER_SPLASH;
                        temp = interSplash;
                    } else if (interIntro != null) {
                        typeStrCache = Constants.INTER_INTRO;
                        temp = interIntro;
                    }
                } else {
                    typeStrCache = Constants.INTER_OTHER;
                    temp = interstitialAdAllFile;
                }
                break;
            }
            default:
                return new Pair<>(null, null);
        }
        return new Pair<>(typeStrCache, temp);
    }



    //get key
    public static String getKeyInter(Context context, String type) {
        if (BuildConfig.DEBUG) return INTER_AD_UNIT;
        switch (type) {
            case Constants.INTER_SPLASH:
                return context.getString(R.string.interstitial_ad_unit_id_splash);
            case Constants.INTER_INTRO:
                return context.getString(R.string.interstitial_ad_unit_id_intro);
            case Constants.INTER_OTHER:
                return context.getString(R.string.interstitial_ad_unit_id_other);
        }
        return "";
    }

    public static String getKeyOpenAd(Context context, String type) {
        if (BuildConfig.DEBUG) return OPEN_APP_AD_UNIT;
        if (Objects.equals(type, Constants.OPEN_SPLASH))
            return context.getString(R.string.App_open_splash);
        else if (Objects.equals(type, Constants.OPEN_RESUME))
            return context.getString(R.string.App_open_ads);
        else
            return "";
    }

    public static String getKeyNative(Context context, String type) {
        if (BuildConfig.DEBUG) return NATIVE_AD_UNIT;
        if (Objects.equals(type, Constants.NATIVE_HOME))
            return context.getString(R.string.Native_Main);
        else if (Objects.equals(type, Constants.NATIVE_OTHER))
            return context.getString(R.string.Native_Dev);
        else if (Objects.equals(type, Constants.NATIVE_WELCOME))
            return context.getString(R.string.Native_welcome);
        else
            return "";
    }

}