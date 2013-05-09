package com.ehc.teluguvelugu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Dictionary extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button search = (Button) findViewById(R.id.button1);
    final TextView text = (TextView) findViewById(R.id.editText1);
    final TextView result = (TextView) findViewById(R.id.editText2);

    AssetManager assertmanager = getAssets();
    Typeface typeFace = Typeface.createFromAsset(assertmanager,
        "Pothana2000.ttf");
    result.setTypeface(typeFace);

    search.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String word = text.getText().toString();
        Log.d("msg", word);

        int flag = 0;

        try {
          InputStream is = getAssets().open("result.csv");
          BufferedReader reader = new BufferedReader(new InputStreamReader(is));
          String line;

          while ((line = reader.readLine()) != null) {
            String[] words = line.split(",");

            if (words[0].equalsIgnoreCase(word)) {
              flag = 1;
              result.setText(words[3]);
              Log.d("*******", words[3]);
              break;
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }

        if (flag == 0) {
          result.setText("sorry, We are unable to find this word");
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
}
