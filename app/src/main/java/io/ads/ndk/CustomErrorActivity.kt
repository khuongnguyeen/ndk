package io.ads.ndk

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.office.crashviewer.config.CaocConfig
import com.office.crashviewer.CustomActivityOnCrash

class CustomErrorActivity : Activity(), View.OnClickListener {
    private var lnlCustomErrorActivityTryAgain: LinearLayout? = null
    private var lnlCustomErrorActivitySendFeedback: LinearLayout? = null
    private var config: CaocConfig? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_error)
        findViews()
        initData()
    }

    private fun initData() {
        lnlCustomErrorActivityTryAgain!!.setOnClickListener(this)
        lnlCustomErrorActivitySendFeedback!!.setOnClickListener(this)
        config = CustomActivityOnCrash.getConfigFromIntent(intent)
        if (config == null) {
            finish()
        }
    }

    private fun findViews() {
        lnlCustomErrorActivityTryAgain = findViewById(R.id.lnl_custom_error_activity_try_again)
        lnlCustomErrorActivitySendFeedback =
            findViewById(R.id.lnl_custom_error_activity_send_feedback)
    }

    override fun onClick(view: View) {
        if (view === lnlCustomErrorActivityTryAgain) {
            CustomActivityOnCrash.restartApplication(this@CustomErrorActivity, config!!)
        } else if (view === lnlCustomErrorActivitySendFeedback) {
            val launcherRateFeedBackDialogs = FeedbackDialog(this)
            launcherRateFeedBackDialogs.show()
        }
    }

    override fun onDestroy() {
        finish()
        super.onDestroy()
    }
}