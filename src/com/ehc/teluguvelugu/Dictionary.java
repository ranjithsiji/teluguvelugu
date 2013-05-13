package com.ehc.teluguvelugu;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

public class Dictionary extends Activity {

  public static Date date = new Date();
  public static String day;
  // public static SharedPreferences sharedPreference;
  public static String word;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Button search = (Button) findViewById(R.id.button1);
    Button random = (Button) findViewById(R.id.button2);
    final SearchView searchview = (SearchView) findViewById(R.id.searchView1);

    final TextView result = (TextView) findViewById(R.id.meaning);

    final Context context = getBaseContext();
    AssetManager assetmanager = getAssets();
    Typeface typeFace = Typeface.createFromAsset(assetmanager,
        "Pothana2000.ttf");
    result.setTypeface(typeFace);
    DataBaseCopy dbcopy = new DataBaseCopy(context, "dictionary.sqlite",
        "com.ehc.teluguvelugu");
    final SQLiteDatabase database = dbcopy.openDataBase();

    day = date.getDate() + "";
    Log.d("dateeeeeeeeeeeeeeeeeeeeeeee", day);
    SharedPreferences sharedPreference = getSharedPreferences("WORDOFDAY", 0);

    String word_day = sharedPreference.getString(day, "");
    if (word_day.equals("")) {

      Cursor cursor = database
          .rawQuery(
              "select * from eng2te where rowid = (abs(random()) % (select max(rowid)+1 from eng2te))",
              null);
      cursor.moveToFirst();

      word = cursor.getString(cursor.getColumnIndex("eng_word")) + " = "
          + cursor.getString(cursor.getColumnIndex("meaning"));
      result.setText(word);
    } else {
      result.setText(word_day);
    }

    search.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String word = searchview.getQuery().toString();
        if (word.equals("")) {
          result.setText("Please Enter a word.");
        } else {
          char array[] = word.toCharArray();
          array[0] = Character.toUpperCase(word.charAt(0));
          String changedword = new String(array);
          Log.d("change word", changedword);

          try {
            Cursor cursor = database.rawQuery(
                "Select * from eng2te where eng_word='" + changedword + "'",
                null);

            if (cursor.moveToFirst()) {
              Log.d(word, cursor.getString(cursor.getColumnIndex("meaning")));
              result.setText(cursor.getString(cursor.getColumnIndex("meaning")));
            } else {
              result.setText("Sorry, we unable to find word");
            }
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }
    });

    random.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        Cursor cursor = database
            .rawQuery(
                "select * from eng2te where rowid = (abs(random()) % (select max(rowid)+1 from eng2te))",
                null);

        cursor.moveToFirst();
        Log.d(cursor.getString(cursor.getColumnIndex("eng_word")),
            cursor.getString(cursor.getColumnIndex("meaning")));

        Intent intent = new Intent(Dictionary.this, RandomActivity.class);
        intent.putExtra("message",
            cursor.getString(cursor.getColumnIndex("eng_word")) + " = "
                + cursor.getString(cursor.getColumnIndex("meaning")));

        startActivity(intent);

      }
    });
  }

  @Override
  protected void onStop() {
    // TODO Auto-generated method stub
    super.onStop();
    SharedPreferences sharedPreference = getSharedPreferences("WORDOFDAY", 0);
    SharedPreferences.Editor editor = sharedPreference.edit();// storing the
    // value
    editor.putString(day, word);
    editor.commit();

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
}
