package io.ads.ndk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.nativead.NativeAd
import io.ads.ndk.ads.AdsUtil
import io.ads.ndk.ads.Constants
import io.ads.ndk.databinding.ActivityMainBinding
import io.sad.monster.ads.Ads
import io.sad.monster.ads.AppOpenManager
import io.sad.monster.callback.AdCallback
import io.sad.monster.callback.DialogExitListener
import io.sad.monster.dialog.AppPurchase
import io.sad.monster.dialog.DialogExitApp
import io.sad.monster.util.Adz
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        AdsUtil.showStaticNativeNormal(
            this,
            AdsUtil.getKeyNative(this, Constants.NATIVE_HOME),
            Constants.NATIVE_HOME,
            binding.nativeContainer
        )
    }

    override fun onResume() {
        super.onResume()
        loadNativeExit()
    }

    private fun loadNativeExit() {
        var x: NativeAd? = Adz.listTypeNative[Constants.NATIVE_EXIT]
        if (x != null) return
        Ads.getInstance().loadNativeAd(
            this,
            AdsUtil.getKeyNative(this, Constants.NATIVE_HOME),
            object : AdCallback() {
                override fun onUnifiedNativeAdLoaded(unifiedNativeAd: NativeAd) {
                    Adz.listTypeNative[Constants.NATIVE_EXIT] = unifiedNativeAd
                }
            })
    }


    override fun onBackPressed() {
        if (!AppPurchase.getInstance()
                .isPurchased(this) && Adz.listTypeNative[Constants.NATIVE_EXIT] != null
        ) {
            AppOpenManager.getInstance()
                .disableAppResumeWithActivity(MainActivity::class.java)
            val dialogExitApp = DialogExitApp(this, Adz.listTypeNative[Constants.NATIVE_EXIT], 1)
            dialogExitApp.setDialogExitListener(object : DialogExitListener {
                override fun onExit(exit: Boolean) {
                    finishAffinity()
                    exitProcess(0)
                }

                override fun onCancel(exit: Boolean) {
                    AppOpenManager.getInstance()
                        .enableAppResumeWithActivity(MainActivity::class.java)
                }
            })
            dialogExitApp.setCancelable(false)
            dialogExitApp.show()
        } else
            super.onBackPressed()
    }


}