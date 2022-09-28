package io.sad.monster.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import io.sad.monster.R;


public class ResumeLoadingDialog extends Dialog {

    public ResumeLoadingDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
