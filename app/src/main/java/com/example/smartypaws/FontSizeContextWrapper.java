package com.example.smartypaws;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

public class FontSizeContextWrapper extends android.content.ContextWrapper {

    public FontSizeContextWrapper(Context base) {
        super(base);
    }

    public static Context wrap(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String textSize = sharedPreferences.getString("TextSize", "Medium"); // Default size

        float fontScale;
        switch (textSize) {
            case "Small":
                fontScale = 0.70f;
                break;
            case "Large":
                fontScale = 1.30f;
                break;
            case "Medium":
            default:
                fontScale = 1.0f;
        }

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.fontScale = fontScale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return new FontSizeContextWrapper(context);
    }
}