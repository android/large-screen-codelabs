package com.example.activity_embedding;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.startup.Initializer;
import androidx.window.embedding.RuleController;
import java.util.Collections;
import java.util.List;

public class SplitInitializer implements Initializer<RuleController> {

    @NonNull
    @Override
    public RuleController create(@NonNull Context context) {
        RuleController ruleController = RuleController.getInstance(context);
        ruleController.setRules(
                RuleController.parseRules(context, R.xml.main_split_config)
        );
        return ruleController;
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return Collections.emptyList();
    }
}
