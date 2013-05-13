package com.ehc.teluguvelugu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash_activity);

    int secondsDelayed = 1;
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        startActivity(new Intent(SplashActivity.this, Dictionary.class));
        finish();
      }
    }, secondsDelayed * 2000);
  }
}
