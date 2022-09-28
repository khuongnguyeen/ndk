package io.ads.ndk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.LoadAdError
import io.ads.ndk.databinding.ActivitySplashBinding
import io.sad.monster.ads.Ads
import io.sad.monster.callback.AdCallback
import io.ads.ndk.ads.AdsUtil
import io.ads.ndk.ads.Constants
import io.sad.monster.util.Adz
import kotlinx.coroutines.Job

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    var job: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
         }

    override fun onResume() {
        super.onResume()
        loadSplashAdOpenApp()
        AdsUtil.loadInterByType(this, Constants.INTER_INTRO)
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

    override fun onBackPressed() {}


    override fun onPause() {
        super.onPause()
        job?.cancel()
    }


    private fun loadSplashAdOpenApp() {

        Ads.getInstance().loadSplashInterstitalAds(
            this,
            AdsUtil.getKeyInter(this, Constants.INTER_SPLASH),
            12000,
            5000,
            object : AdCallback() {
                override fun onAdClosed() {
                    launchMain()
                }

                override fun onAdFailedToLoad(i: LoadAdError?) {
                    launchMain()
                }
            })
    }



    private fun call() {
        var  mIntent : Intent = Intent(this, MainActivity::class.java)

        mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mIntent)
        finish()
    }


    private fun launchMain() {
        call()
    }
}

