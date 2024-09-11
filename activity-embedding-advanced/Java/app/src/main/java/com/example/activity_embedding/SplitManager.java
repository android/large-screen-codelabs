package com.example.activity_embedding;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.window.embedding.ActivityFilter;
import androidx.window.embedding.ActivityRule;
import androidx.window.embedding.RuleController;
import androidx.window.embedding.SplitAttributes;
import androidx.window.embedding.SplitPairFilter;
import androidx.window.embedding.SplitPairRule;
import androidx.window.embedding.SplitPlaceholderRule;
import androidx.window.embedding.SplitRule;

import java.util.HashSet;
import java.util.Set;

public class SplitManager {
    static void createSplit(Context context) {

        SplitPairFilter splitPairFilter = new SplitPairFilter(
                new ComponentName(context, ListActivity.class),
                new ComponentName(context, DetailActivity.class),
                null
        );

        Set<SplitPairFilter> filterSet = new HashSet<>();
        filterSet.add(splitPairFilter);

        SplitAttributes splitAttributes = new SplitAttributes.Builder()
                .setSplitType(SplitAttributes.SplitType.ratio(0.33f))
                .setLayoutDirection(SplitAttributes.LayoutDirection.LEFT_TO_RIGHT)
                .build();

        SplitPairRule splitPairRule = new SplitPairRule.Builder(filterSet)
                .setDefaultSplitAttributes(splitAttributes)
                .setMinWidthDp(840)
                .setMinSmallestWidthDp(600)
                .setFinishPrimaryWithSecondary(SplitRule.FinishBehavior.NEVER)
                .setFinishSecondaryWithPrimary(SplitRule.FinishBehavior.ALWAYS)
                .setClearTop(false)
                .build();

        RuleController ruleController = RuleController.getInstance(context);
        ruleController.addRule(splitPairRule);

        ActivityFilter placeholderActivityFilter = new ActivityFilter(
                new ComponentName(context, ListActivity.class),
                null
        );

        Set<ActivityFilter> placeholderActivityFilterSet = new HashSet<>();
        placeholderActivityFilterSet.add(placeholderActivityFilter);

        SplitPlaceholderRule splitPlaceholderRule = new SplitPlaceholderRule.Builder(
                placeholderActivityFilterSet,
                new Intent(context, PlaceholderActivity.class)
        ).setDefaultSplitAttributes(splitAttributes)
                .setMinWidthDp(840)
                .setMinSmallestWidthDp(600)
                .setFinishPrimaryWithPlaceholder(SplitRule.FinishBehavior.ALWAYS)
                .build();

        ruleController.addRule(splitPlaceholderRule);

        ActivityFilter summaryActivityFilter = new ActivityFilter(
                new ComponentName(context, SummaryActivity.class),
                null
        );
        Set<ActivityFilter> summaryActivityFilterSet = new HashSet<>();
        summaryActivityFilterSet.add(summaryActivityFilter);
        ActivityRule activityRule = new ActivityRule.Builder(
                summaryActivityFilterSet
        ).setAlwaysExpand(true).build();
        ruleController.addRule(activityRule);

    }
}
