package com.android.example.leanback.fastlane;

import android.app.Activity;
import android.os.Bundle;

import com.android.example.leanback.R;


public class LeanbackActivity extends Activity {
    public final static String VIDEO = "VIDEO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leanback);
    }
}
