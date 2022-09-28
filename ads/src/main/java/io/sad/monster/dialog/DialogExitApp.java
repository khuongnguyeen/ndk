package io.sad.monster.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import io.sad.monster.R;
import io.sad.monster.ads.Ads;
import io.sad.monster.callback.DialogExitListener;

public class DialogExitApp extends Dialog {

    private final Context context;
    private final NativeAd nativeAd;
    private final int type;
    private FrameLayout frameLayout;
    private NativeAdView adView;
    private TextView btnExit;
    private TextView btnCancel;
    private DialogExitListener dialogExitListener;

    public DialogExitApp(Context context, NativeAd nativeAd, int type) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.nativeAd = nativeAd;
        this.type = type;
    }

    public DialogExitApp(Context context, NativeAd nativeAd, int type, NativeAdView adView) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.nativeAd = nativeAd;
        this.type = type;
        this.adView = adView;
    }


    public void setDialogExitListener(DialogExitListener dialogExitListener) {
        this.dialogExitListener = dialogExitListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (type == 1) {
            setContentView(R.layout.view_dialog_exit1);
        } else if (type == 2) {
            setContentView(R.layout.view_dialog_exit2);
        } else {
            setContentView(R.layout.view_dialog_exit3);
        }
        initView();
    }

    private void initView() {
        btnExit = findViewById(R.id.btnExit);
        btnCancel = findViewById(R.id.btnCancel);
        frameLayout = findViewById(R.id.frAds);

        if (adView == null) {
            if (type == 1) {
                adView = (NativeAdView) LayoutInflater.from(context)
                    .inflate(R.layout.native_exit1, null);
            } else if (type == 2) {
                adView = (NativeAdView) LayoutInflater.from(context)
                    .inflate(R.layout.native_exit1, null);
            } else {
                adView = (NativeAdView) LayoutInflater.from(context)
                    .inflate(R.layout.native_exit1, null);
            }
        }
        frameLayout.addView(adView);

        Ads.getInstance().populateUnifiedNativeAdView(nativeAd, adView);

        btnExit.setOnClickListener(v -> {
            dismiss();
            if (dialogExitListener != null) {
                dialogExitListener.onExit(true);
            } else {
                if(context != null) {
                    ((Activity) context).finish();
                }
            }
        });
        btnCancel.setOnClickListener(v -> {
            if (dialogExitListener != null) {
                dialogExitListener.onCancel(false);
            }
            dismiss();
        });
    }
}