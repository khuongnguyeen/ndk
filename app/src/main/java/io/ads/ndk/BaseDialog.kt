package io.ads.ndk

import android.app.Dialog
import android.content.Context
import android.os.Bundle

abstract class BaseDialog(private val mContext: Context) : Dialog(
    mContext
) {
    protected abstract fun initView()
    protected abstract fun initData()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }
}