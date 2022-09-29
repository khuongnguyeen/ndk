package io.ads.ndk

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import io.ads.ndk.AppUtils.sendFeedBack

class FeedbackDialog(context: Context) : BaseDialog(context), View.OnClickListener {
    private val mContext: Context
    private var txvDialogRateFeedbackTitle: TextView? = null
    private var txvDialogRateContent: TextView? = null
    private var edtDialogRateFeedbackTitle: EditText? = null
    private var btnDialogFeedbackSend: TextView? = null
    private var tvCancelDialog: TextView? = null
    override fun initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(true)
        setContentView(R.layout.dialog_rate_feedback)
        funcStyle()
        findViews()
    }

    private fun funcStyle() {
        val window = window
        window!!.setGravity(Gravity.CENTER)
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setWindowAnimations(R.style.anim_open_dialog)
        setCancelable(true)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun initData() {}
    private fun findViews() {
        txvDialogRateFeedbackTitle = findViewById(R.id.txv_dialog_rate_feedback_title)
        txvDialogRateContent = findViewById(R.id.txv_dialog_rate_content)
        edtDialogRateFeedbackTitle = findViewById(R.id.edt_dialog_rate_feedback_title)
        tvCancelDialog = findViewById(R.id.tv_cancel)
        btnDialogFeedbackSend = findViewById(R.id.btn_dialog_feedback_send)
        btnDialogFeedbackSend!!.setOnClickListener(this)
        tvCancelDialog!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v === btnDialogFeedbackSend) {
            if (edtDialogRateFeedbackTitle!!.text.toString().isEmpty()) {
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.fill_feedback),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                var pInfo: PackageInfo? = null
                try {
                    pInfo = mContext.packageManager.getPackageInfo(mContext.packageName, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
                var currentAppVersionCode = 0
                if (pInfo != null) {
                    currentAppVersionCode = pInfo.versionCode
                }
                sendFeedBack(
                    mContext,
                    "khuongfather@gmail.com",
                    edtDialogRateFeedbackTitle!!.text.toString() + mContext.resources.getString(
                        R.string.rate_content_sign
                    ) + currentAppVersionCode
                )
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.thank_share),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        } else if (v === tvCancelDialog) {
            dismiss()
        }
    }

    init {
        mContext = context.applicationContext
    }
}