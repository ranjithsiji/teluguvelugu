package com.ehc.teluguvelugu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

public class Dictionary extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Button search = (Button) findViewById(R.id.button1);
    Button random = (Button) findViewById(R.id.button2);
    final TextView text = (TextView) findViewById(R.id.editText1);
    final TextView result = (TextView) findViewById(R.id.editText2);
    final Context context = getBaseContext();
    AssetManager assertmanager = getAssets();
    Typeface typeFace = Typeface.createFromAsset(assertmanager,
        "Pothana2000.ttf");
    result.setTypeface(typeFace);
    DataBaseCopy dbcopy = new DataBaseCopy(context, "dictionary.sqlite",
        "com.ehc.teluguvelugu");
    final SQLiteDatabase database = dbcopy.openDataBase();

    // search.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // String word = text.getText().toString();
    // Log.d("msg", word);
    //
    // int flag = 0;
    //
    // try {
    // InputStream is = getAssets().open("result.csv");
    // BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    // String line;
    //
    // while ((line = reader.readLine()) != null) {
    // String[] words = line.split(",");
    //
    // if (words[0].equalsIgnoreCase(word)) {
    // flag = 1;
    // result.setText(words[3]);
    // Log.d("*******", words[3]);
    // break;
    // }
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // if (flag == 0) {
    // result.setText("sorry, We are unable to find this word");
    // }
    // }
    // });
    search.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String word = text.getText().toString();
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
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
}
