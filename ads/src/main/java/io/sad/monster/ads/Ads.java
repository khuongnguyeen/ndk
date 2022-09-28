package io.sad.monster.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.Calendar;

import io.sad.monster.BuildConfig;
import io.sad.monster.R;
import io.sad.monster.callback.AdCallback;
import io.sad.monster.callback.AdmodHelper;
import io.sad.monster.dialog.AppPurchase;
import io.sad.monster.dialog.PrepareLoadingAdsDialog;
import io.sad.monster.util.FirebaseAnalyticsUtil;

public class Ads {
    private static final String TAG = "Admob";
    @SuppressLint("StaticFieldLeak")
    private static Ads instance;
    private int currentClicked = 0;
    private int numShowAds = 0;
    private final int maxClickAds = 100;
    private PrepareLoadingAdsDialog dialog;
    private boolean openActivityAfterShowInterAds = false;
    private Context context;

    private Ads() {
    }

    public static Ads getInstance() {
        if (instance == null) {
            instance = new Ads();
        }
        return instance;
    }


    public void setNumToShowAds(int numShowAds) {
        this.numShowAds = numShowAds;
        this.currentClicked = numShowAds - 1;
    }


    public void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = Application.getProcessName();
            String packageName = context.getPackageName();
            if (!packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }

        MobileAds.initialize(context, initializationStatus -> {
        });

        this.context = context;
    }

    public void setOpenActivityAfterShowInterAds(boolean openActivityAfterShowInterAds) {
        this.openActivityAfterShowInterAds = openActivityAfterShowInterAds;
    }

    public AdRequest getAdRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
        return builder.build();
    }

    public void getInterstitialAds(Context context, String id, AdCallback adCallback) {

        if (AppPurchase.getInstance().isPurchased(context) || AdmodHelper.getNumClickAdsPerDay(context, id) >= maxClickAds) {
            adCallback.onInterstitialLoad(null);
            return;
        }
        try {
            InterstitialAd.load(context, id, getAdRequest(),
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            if (adCallback != null)
                                adCallback.onInterstitialLoad(interstitialAd);
                            interstitialAd.setOnPaidEventListener(adValue -> {
                                Log.e("FirebaseAnalyticsUtil", "OnPaidEvent getInterstitialAds:" + adValue.getValueMicros());
                                FirebaseAnalyticsUtil.logPaidAdImpression(context,
                                        adValue,
                                        interstitialAd.getAdUnitId(),
                                        interstitialAd.getResponseInfo()
                                                .getMediationAdapterClassName() + "_interstitial");
                            });

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            if (adCallback != null)
                                adCallback.onAdFailedToLoad(loadAdError);
                        }

                    });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void showInterstitialAdByTimes(final Context context, InterstitialAd interstitialAd, final AdCallback callback) {
      try {
          AdmodHelper.setupAdmodData(context);
          if (AppPurchase.getInstance().isPurchased(context)) {
              callback.onAdClosed();
              callback.onAdDismissedFullScreenContent();
              return;
          }
          if (interstitialAd == null) {
              if (callback != null) {
                  callback.onAdClosed();
                  callback.onAdDismissedFullScreenContent();
              }
              return;
          }

          interstitialAd.setOnPaidEventListener(adValue -> {
              Log.e("FirebaseAnalyticsUtil", "OnPaidEvent loadInterstitialAds:" + adValue.getValueMicros());
              FirebaseAnalyticsUtil.logPaidAdImpression(context,
                      adValue,
                      interstitialAd.getAdUnitId(),
                      interstitialAd.getResponseInfo()
                              .getMediationAdapterClassName() + "_interstitial");
          });


          interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

              @Override
              public void onAdDismissedFullScreenContent() {
                  super.onAdDismissedFullScreenContent();
                  if (AppOpenManager.getInstance().isInitialized()) {
                      AppOpenManager.getInstance().enableAppResume();
                  }

                  if (callback != null) {
                      callback.onAdDismissedFullScreenContent();
                  }
                  if (dialog != null) {
                      dialog.dismiss();
                  }
                  Log.e(TAG, "onAdDismissedFullScreenContent");
              }

              @Override
              public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                  super.onAdFailedToShowFullScreenContent(adError);
                  if (!openActivityAfterShowInterAds) {
                      if (callback != null) {
                          callback.onAdClosed();
                      }
                  }
                  if (callback != null) {
                      callback.onAdFailedToShow(adError);
                  }

                  if (dialog != null) {
                      dialog.dismiss();
                  }
              }

              @Override
              public void onAdShowedFullScreenContent() {
                  super.onAdShowedFullScreenContent();
                  // Called when fullscreen content is shown.
                  if (callback != null) {
                      callback.onAdShowedFullScreenContent();
                  }
              }

              @Override
              public void onAdClicked() {
                  super.onAdClicked();
                  FirebaseAnalyticsUtil.logClickAdsEvent(context, interstitialAd.getAdUnitId());
              }
          });

          if (AdmodHelper.getNumClickAdsPerDay(context, interstitialAd.getAdUnitId()) < maxClickAds) {
              showInterstitialAd(context, interstitialAd, callback);
          }
      }catch (Throwable e){
          callback.onAdClosed();
          callback.onAdDismissedFullScreenContent();
      }
    }

    public void forceShowInterstitial(Context context, InterstitialAd mInterstitialAd, final AdCallback callback) {
        showInterstitialAdByTimes(context, mInterstitialAd, callback);
    }

    private void showInterstitialAd(Context context, InterstitialAd interstitialAd, AdCallback callback) {
        currentClicked++;
        if (currentClicked >= numShowAds && interstitialAd != null) {
            if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                try {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    dialog = new PrepareLoadingAdsDialog(context);
                    dialog.show();
                } catch (Exception e) {
                    dialog = null;
                    e.printStackTrace();
                    return;
                }
                new Handler().postDelayed(() -> {
                    if (AppOpenManager.getInstance().isInitialized()) {
                        AppOpenManager.getInstance().disableAppResume();
                    }

                    if (openActivityAfterShowInterAds && callback != null) {
                        callback.onAdClosed();
                        new Handler().postDelayed(() -> {
                            if (dialog != null && dialog.isShowing() && !((Activity) context).isDestroyed())
                                dialog.dismiss();
                        }, 1500);
                    }
                    if (context != null && !((Activity) context).isDestroyed()) {
                        interstitialAd.show((Activity) context);
                    }

                }, 800);

            }
            currentClicked = 0;
        } else {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (callback != null) {
                callback.onAdClosed();
            }
        }
    }

    public void loadNativeAd(Context context, final String id, final AdCallback callback) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            if (callback != null) {
                callback.onAdClosed();
            }
            return;
        }
        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        try {

            AdLoader adLoader = new AdLoader.Builder(context, id)
                    .forNativeAd(nativeAd -> {
                        if (callback != null) {
                            callback.onUnifiedNativeAdLoaded(nativeAd);
                        }
                        nativeAd.setOnPaidEventListener(adValue -> {
                            Log.e("FirebaseAnalyticsUtil", "OnPaidEvent Native:" + adValue.getValueMicros());
                            FirebaseAnalyticsUtil.logPaidAdImpression(context, adValue, id, "native");
                        });
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError error) {
                            Log.e(TAG, "NativeAd onAdFailedToLoad: " + error.getMessage());
                            callback.onAdFailedToLoad(error);
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            if (callback != null) {
                                callback.onAdClicked();
                                Log.e(TAG, "onAdClicked");
                            }
                            FirebaseAnalyticsUtil.logClickAdsEvent(context, id);
                        }
                    })
                    .withNativeAdOptions(adOptions)
                    .build();
            adLoader.loadAd(getAdRequest());
        } catch (Throwable ex) {
            ex.printStackTrace();
            callback.onAdFailedToLoad(null);
        }
    }

    public void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {

        adView.setMediaView(adView.findViewById(R.id.ad_media));
        MediaView mediaView = adView.getMediaView();
        if (mediaView != null) {
            mediaView.postDelayed(() -> {
                if (context != null && BuildConfig.DEBUG) {
                    float sizeMin = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            120,
                            context.getResources().getDisplayMetrics()
                    );
                    Log.e(TAG, "Native sizeMin: " + sizeMin);
                    Log.e(TAG, "Native w/h media : " + mediaView.getWidth() + "/" + mediaView.getHeight());
                    if (mediaView.getWidth() < sizeMin || mediaView.getHeight() < sizeMin) {
                        Log.e(TAG, "Size media native not valid: " + "WIDTH_VIEW_:" + mediaView.getWidth() + "HEIGHT_VIEW_:" + mediaView.getHeight() + "_WIDTH_MIN_:" + sizeMin);
                    }
                }
            }, 1000);

        }
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));

        View tv = adView.getHeadlineView();
        if (tv instanceof TextView && nativeAd != null) {
            ((TextView) tv).setText(nativeAd.getHeadline());
        }
        if (nativeAd != null) {
            View bodyView = adView.getBodyView();
            if (bodyView != null) {
                if (nativeAd.getBody() == null) {
                    bodyView.setVisibility(View.INVISIBLE);
                } else {
                    bodyView.setVisibility(View.VISIBLE);
                    if (bodyView instanceof TextView)
                        ((TextView) bodyView).setText(nativeAd.getBody());
                }
            }

            View actionView = adView.getCallToActionView();
            if (actionView != null) {
                if (nativeAd.getCallToAction() == null) {
                    actionView.setVisibility(View.INVISIBLE);
                } else {
                    actionView.setVisibility(View.VISIBLE);
                    if (actionView instanceof TextView) {
                        ((TextView) actionView).setText(nativeAd.getCallToAction());
                    }
                }
            }

            View iconView = adView.getIconView();
            if (iconView != null) {
                if (nativeAd.getIcon() == null) {
                    iconView.setVisibility(View.GONE);
                } else {
                    iconView.setVisibility(View.VISIBLE);
                    if (iconView instanceof ImageView) {
                        ((ImageView) iconView).setImageDrawable(
                                nativeAd.getIcon().getDrawable());
                    }
                }
            }

            View adPriceView = adView.getPriceView();
            if (adPriceView != null) {
                if (nativeAd.getPrice() == null) {
                    adPriceView.setVisibility(View.INVISIBLE);
                } else {
                    adPriceView.setVisibility(View.VISIBLE);
                    if (adPriceView instanceof TextView) {
                        ((TextView) adPriceView).setText(nativeAd.getPrice());
                    }
                }
            }

            View storeView = adView.getStoreView();
            if (storeView != null) {
                if (nativeAd.getStore() == null) {
                    storeView.setVisibility(View.INVISIBLE);
                } else {
                    storeView.setVisibility(View.VISIBLE);
                    if (storeView instanceof TextView) {
                        ((TextView) storeView).setText(nativeAd.getStore());
                    }
                }
            }

            View startRatingView = adView.getStarRatingView();
            if (startRatingView != null) {
                if (nativeAd.getStarRating() == null) {
                    startRatingView.setVisibility(View.INVISIBLE);
                } else {
                    startRatingView.setVisibility(View.VISIBLE);
                    if (startRatingView instanceof RatingBar) {
                        ((RatingBar) adView.getStarRatingView())
                                .setRating(nativeAd.getStarRating().floatValue());
                    }
                }
            }

            View advertiserView = adView.getAdvertiserView();
            if (advertiserView != null) {
                if (nativeAd.getAdvertiser() == null) {
                    advertiserView.setVisibility(View.INVISIBLE);
                } else {
                    advertiserView.setVisibility(View.VISIBLE);
                    if (advertiserView instanceof TextView) {
                        ((TextView) advertiserView).setText(nativeAd.getAdvertiser());
                    }
                }
            }

            adView.setNativeAd(nativeAd);
        }

    }


    /**
     * Load quảng cáo Full tại màn SplashActivity
     * Sau khoảng thời gian timeout thì load ads và callback về cho View
     *
     * @param context
     * @param id
     * @param timeOut    : thời gian chờ ads, timeout <= 0 tương đương với việc bỏ timeout
     * @param timeDelay  : thời gian chờ show ad từ lúc load ads
     * @param adListener
     */


    private boolean isShowLoadingSplash = false;  //kiểm tra trạng thái ad splash, ko cho load, show khi đang show loading ads splash
    private Handler handlerTimeout;
    private Runnable rdTimeout;
    InterstitialAd mInterstitialSplash;
    private boolean isTimeout; // xử lý timeout show ads
    boolean isTimeDelay = false; //xử lý delay time show ads, = true mới show ads

    public void loadSplashInterstitalAds(final Context context, String id, long timeOut, long timeDelay, AdCallback adListener) {
        isTimeDelay = false;
        isTimeout = false;

        if (AppPurchase.getInstance().isPurchased(context)) {
            if (adListener != null) {
                adListener.onAdClosed();
            }
            return;
        }
        new Handler().postDelayed(() -> {
            //check delay show ad splash
            if (mInterstitialSplash != null) {
                Log.i(TAG, "loadSplashInterstitalAds:show ad on delay ");
                onShowSplash((Activity) context, adListener);
                return;
            }
            Log.i(TAG, "loadSplashInterstitalAds: delay validate");
            isTimeDelay = true;
        }, timeDelay);

        if (timeOut > 0) {
            handlerTimeout = new Handler();
            rdTimeout = () -> {
                Log.e(TAG, "loadSplashInterstitalAds: on timeout");
                isTimeout = true;
                if (mInterstitialSplash != null) {
                    Log.i(TAG, "loadSplashInterstitalAds:show ad on timeout ");
                    onShowSplash((Activity) context, adListener);
                    return;
                }
                if (adListener != null) {
                    adListener.onAdClosed();
                    isShowLoadingSplash = false;
                }
            };
            handlerTimeout.postDelayed(rdTimeout, timeOut);
        }

//        if (isShowLoadingSplash)
//            return;
        isShowLoadingSplash = true;
        getInterstitalAds(context, id, new AdCallback() {
            @Override
            public void onInterstitialLoad(InterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                Log.e(TAG, "loadSplashInterstitalAds  end time loading success:" + Calendar.getInstance().getTimeInMillis() + "     time limit:" + isTimeout);
                if (isTimeout)
                    return;
                if (interstitialAd != null) {
                    mInterstitialSplash = interstitialAd;
                    if (isTimeDelay) {
                        onShowSplash((Activity) context, adListener);
                        Log.i(TAG, "loadSplashInterstitalAds:show ad on loaded ");
                    }
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError i) {
                super.onAdFailedToLoad(i);
                Log.e(TAG, "loadSplashInterstitalAds  end time loading error:" + Calendar.getInstance().getTimeInMillis() + "     time limit:" + isTimeout);
                if (isTimeout)
                    return;
                if (adListener != null) {
                    if (handlerTimeout != null && rdTimeout != null) {
                        handlerTimeout.removeCallbacks(rdTimeout);
                    }
                    if (i != null)
                        Log.e(TAG, "loadSplashInterstitalAds: load fail " + i.getMessage());
                    adListener.onAdFailedToLoad(i);
                }
            }
        });

    }

    private void onShowSplash(Activity activity, AdCallback adListener) {
        isShowLoadingSplash = true;


        if (mInterstitialSplash != null) {
            mInterstitialSplash.setOnPaidEventListener(adValue -> {
                Log.d(TAG, "OnPaidEvent splash:" + adValue.getValueMicros());

            });
        }
        if (handlerTimeout != null && rdTimeout != null) {
            handlerTimeout.removeCallbacks(rdTimeout);
        }

        if (adListener != null) {
            adListener.onAdLoaded();
        }

        mInterstitialSplash.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                isShowLoadingSplash = false;
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                if (AppOpenManager.getInstance().isInitialized()) {
                    AppOpenManager.getInstance().enableAppResume();
                }
                if (adListener != null) {
                    if (!openActivityAfterShowInterAds) {
                        adListener.onAdClosed();
                    }

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
                mInterstitialSplash = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                mInterstitialSplash = null;
                isShowLoadingSplash = false;
                if (adListener != null) {
                    if (!openActivityAfterShowInterAds) {
                        adListener.onAdFailedToShow(adError);
                    }

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();

            }
        });

        if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            try {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                dialog = new PrepareLoadingAdsDialog(activity);
                try {
                    dialog.show();
                } catch (Exception e) {
                    adListener.onAdClosed();
                    return;
                }
            } catch (Exception e) {
                dialog = null;
                e.printStackTrace();
            }
            new Handler().postDelayed(() -> {
                if (AppOpenManager.getInstance().isInitialized()) {
                    AppOpenManager.getInstance().disableAppResume();
                }

                if (openActivityAfterShowInterAds && adListener != null) {
                    adListener.onAdClosed();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null && dialog.isShowing() && !activity.isDestroyed())
                                dialog.dismiss();
                        }
                    }, 1500);
                }
                if (activity != null)
                    mInterstitialSplash.show(activity);
                isShowLoadingSplash = false;
            }, 800);

        }
    }


    public void getInterstitalAds(Context context, String id, AdCallback adCallback) {

        if (AppPurchase.getInstance().isPurchased(context) || AdmodHelper.getNumClickAdsPerDay(context, id) >= maxClickAds) {
            adCallback.onInterstitialLoad(null);
            return;
        }


        InterstitialAd.load(context, id, getAdRequest(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        if (adCallback != null)
                            adCallback.onInterstitialLoad(interstitialAd);

                        //tracking adjust
                        interstitialAd.setOnPaidEventListener(adValue -> {

                            Log.d(TAG, "OnPaidEvent getInterstitalAds:" + adValue.getValueMicros());

                            FirebaseAnalyticsUtil.logPaidAdImpression(context,
                                    adValue,
                                    interstitialAd.getAdUnitId(),
                                    interstitialAd.getResponseInfo()
                                            .getMediationAdapterClassName());
                        });
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        if (adCallback != null)
                            adCallback.onAdFailedToLoad(loadAdError);
                    }

                });

    }




}