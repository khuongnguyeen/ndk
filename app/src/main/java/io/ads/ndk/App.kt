package io.ads.ndk

import android.app.Application
import io.ads.ndk.ads.AdsUtil
import io.ads.ndk.ads.Constants
import io.sad.monster.ads.application.AdsApplication
import io.sad.monster.util.Adz

class App:AdsApplication() {

    override fun onCreate() {
        super.onCreate()
        Adz.setTimeReloadNative(60000)
        Adz.listTypeInter[Constants.INTER_INTRO] = null
        Adz.listTypeInter[Constants.INTER_SPLASH] = null
        Adz.listTypeInter[Constants.INTER_OTHER] = null

        Adz.listTypeNative[Constants.NATIVE_EXIT] = null
        Adz.listTypeNative[Constants.NATIVE_HOME] = null
        Adz.listTypeNative[Constants.NATIVE_OTHER] = null
        Adz.listTypeNative[Constants.NATIVE_WELCOME] = null
    }


    override fun enableAdsResume() = true

    override fun getOpenResumeAppAdId() = AdsUtil.getKeyOpenAd(this,Constants.OPEN_RESUME)!!
}