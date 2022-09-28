package io.ads.ndk

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import io.ads.ndk.ads.AdsUtil
import io.ads.ndk.ads.Constants
import io.sad.monster.ads.Ads
import io.sad.monster.callback.AdCallback
import io.sad.monster.util.Adz

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        supportActionBar?.hide()

        findViewById<TextView>(R.id.tvNext).setOnClickListener {
                showAds();
        }

        AdsUtil.showStaticNativeNormal(
            this,
            AdsUtil.getKeyNative(this, Constants.NATIVE_WELCOME),
            Constants.NATIVE_WELCOME,
            findViewById(R.id.native_container)
        )
    }
    private fun showAds(){
        var ads = AdsUtil.getInterstitialAdsForShow(Constants.INTER_INTRO)
        if (ads.second == null) {
            callMain()
            return;
        }
        Ads.getInstance().forceShowInterstitial(
            this,
            ads.second,
            object : AdCallback() {

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    callMain()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    Adz.clearAds(ads.first)
                }

                override fun onAdFailedToShow(adError: AdError?) {
                    super.onAdFailedToShow(adError)
                    Adz.clearAds(ads.first)
                    callMain();
                }
            })
    }

    private fun callMain(){
        val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
