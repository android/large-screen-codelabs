package com.example.activity_embedding;

import android.app.Application;
import androidx.window.embedding.RuleController;

/**
 * Initializer for activity embedding split rules.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SplitManager.createSplit(this);
    }
}
