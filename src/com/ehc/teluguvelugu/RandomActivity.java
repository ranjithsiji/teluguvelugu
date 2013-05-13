package com.ehc.teluguvelugu;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class RandomActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_random);

    TextView view = (TextView) findViewById(R.id.random);
    AssetManager assertmanager = getAssets();
    Typeface typeFace = Typeface.createFromAsset(assertmanager,
        "Pothana2000.ttf");
    view.setTypeface(typeFace);
    view.setText(getIntent().getExtras().getString("message"));
  }
}
