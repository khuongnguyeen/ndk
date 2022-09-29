package io.ads.ndk

import android.content.Context
import android.content.Intent

object AppUtils {

    @JvmStatic
    fun sendFeedBack(context: Context, email: String, content: String?) {
        val email2 = Intent(Intent.ACTION_SEND)
        email2.type = "text/email"
        email2.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        email2.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.rate_title))
        email2.putExtra(Intent.EXTRA_TEXT, content)
        val chooserIntent = Intent.createChooser(email2, "Send Feedback:")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }

}