package com.office.crashviewer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class ToolsAll {
    private static Toast myToast;

    public static Drawable getDrawableByName(Context context, String name) {
        return context.getResources().getDrawable(context.getResources().getIdentifier(name, "drawable", context.getPackageName()));
    }

    public static void launchToMarketAppPro(Context mContext, String paketname) {
        try {
            mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + paketname)));
        } catch (ActivityNotFoundException e) {
            try {
                mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + paketname + "&referrer=utm_source%3Dpiano_smi")));
            } catch (Exception e2) {
                showToast(mContext, "unable to find market app");
            }
        }
    }

    public static void showToast(Context mContext, String text) {
        Toast toast = myToast;
        if (toast != null) {
            toast.cancel();
        } else {
            myToast = new Toast(mContext);
        }
        myToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        myToast.show();
    }

    public static int findViewId(Context context, String nameId) {
        return context.getResources().getIdentifier(nameId, "id", context.getPackageName());
    }

    public static int getIdLayout(Context context, String nameLayout) {
        return context.getResources().getIdentifier(nameLayout, "layout", context.getPackageName());
    }

    public static int getIdStyle(Context context, String nameLayout) {
        return context.getResources().getIdentifier(nameLayout, "style", context.getPackageName());
    }

    public static int getIdStyleable(Context context, String nameLayout) {
        return context.getResources().getIdentifier(nameLayout, "styleable", context.getPackageName());
    }

    public static int getIdColor(Context context, String nameLayout) {
        return context.getResources().getIdentifier(nameLayout, "color", context.getPackageName());
    }

    public static int getIdFromRaw(Context context, String nameLayout) {
        return context.getResources().getIdentifier(nameLayout, "raw", context.getPackageName());
    }

    public static AnimationDrawable getFrame(Context mcontext, int number) {
        AnimationDrawable animationDrawable = new AnimationDrawable();
        DecimalFormat formatter = new DecimalFormat("00");
        for (int i = 0; i < number; i++) {
            String aFormatted = formatter.format((long) (i + 1));
            Resources resources = mcontext.getResources();
            animationDrawable.addFrame(resources.getDrawable(getIdDrawable(mcontext, "gift_animation_" + aFormatted)), 100);
        }
        return animationDrawable;
    }

    public static int getIdDrawable(Context context, String nameLayout) {
        return context.getResources().getIdentifier(nameLayout, "drawable", context.getPackageName());
    }

    public static int getIdString(Context context, String nameLayout) {
        return context.getResources().getIdentifier(nameLayout, "string", context.getPackageName());
    }

    public static int getIdMipmap(Context context, String nameLayout) {
        return context.getResources().getIdentifier(nameLayout, "mipmap", context.getPackageName());
    }

    public static int getIdFromAnim(Context context, String namAnim) {
        return context.getResources().getIdentifier(namAnim, "anim", context.getPackageName());
    }

    public static int getIdFromAsset(Context context, String nameAsset) {
        return context.getResources().getIdentifier(nameAsset, "raw", context.getPackageName());
    }

    public static void setFontForTextView(Context context, TextView view) {
        view.setTypeface(Typeface.createFromAsset(context.getAssets(), "newfonts/product-sans.ttf"));
    }

    public static void setFontForButton(Context context, Button view) {
        view.setTypeface(Typeface.createFromAsset(context.getAssets(), "newfonts/product-sans.ttf"));
    }

    public static void goneView(Activity activity, String name) {
        activity.findViewById(findViewId(activity, name)).setVisibility(View.GONE);
    }

    public static void visibleView(Activity activity, String name) {
        activity.findViewById(findViewId(activity, name)).setVisibility(View.VISIBLE);
    }

    public static boolean checkInstalled(String packageName, Context context) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void gotoMarket(Context context) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void showEmailFeedback(Activity activity) {
        Intent feedbackEmail = new Intent(Intent.ACTION_SEND);
        feedbackEmail.setType("text/email");
        feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"docxreaderappro@gmail.com"});
        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Feed Back");
        activity.startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"));
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }


    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }
}
