package com.office.crashviewer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.office.crashviewer.CustomActivityOnCrash;
import com.office.crashviewer.ToolsAll;
import com.office.crashviewer.config.CaocConfig;

public final class DefaultErrorActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ToolsAll.getIdLayout(this, "customactivityoncrash_default_error_activity"));

        Button restartButton = findViewById(ToolsAll.findViewId(this, "customactivityoncrash_error_activity_restart_button"));
        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        if (config == null) {
            finish();
            return;
        }

        Log.e("DefaultErrorActivity", "AAAAAAAAAAAAAAAAAAAAAAA: " + CustomActivityOnCrash.getAllErrorDetailsFromIntent(DefaultErrorActivity.this, getIntent()));

        if (config.isShowRestartButton() && config.getRestartActivityClass() != null) {
            restartButton.setText(ToolsAll.getIdString(this, "customactivityoncrash_error_activity_restart_app"));
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomActivityOnCrash.restartApplication(DefaultErrorActivity.this, config);
                }
            });
        } else {
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomActivityOnCrash.closeApplication(DefaultErrorActivity.this, config);
                }
            });
        }

        Integer defaultErrorActivityDrawableId = config.getErrorDrawable();
        ImageView errorImageView = findViewById(ToolsAll.findViewId(DefaultErrorActivity.this, "customactivityoncrash_error_activity_image"));

        if (defaultErrorActivityDrawableId != null) {
            errorImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), defaultErrorActivityDrawableId, getTheme()));
        }
    }
}
