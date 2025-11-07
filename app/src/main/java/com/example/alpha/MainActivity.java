package com.example.alpha;

import android.os.Bundle;

/**
 * The main activity of the application.
 * It extends BaseActivity to inherit the common options menu.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FBRef.refAuth.signOut();
    }
}
