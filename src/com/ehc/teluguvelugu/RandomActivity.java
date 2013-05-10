package com.ehc.teluguvelugu;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class RandomActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_random);

    TextView view = (TextView) findViewById(R.id.editText1);
    Context context = getBaseContext();

    DataBaseCopy dbcopy = new DataBaseCopy(context, "dictionary.sqlite",
        "com.ehc.teluguvelugu");
    SQLiteDatabase database = dbcopy.openDataBase();

    Cursor cursor = database
        .rawQuery(
            "select * from eng2te where rowid = (abs(random()) % (select max(rowid)+1 from eng2te))",
            null);

    cursor.moveToFirst();
    Log.d(cursor.getString(cursor.getColumnIndex("eng_word")),
        cursor.getString(cursor.getColumnIndex("meaning")));
    view.setText(cursor.getString(cursor.getColumnIndex("eng_word")) + "\n"
        + cursor.getString(cursor.getColumnIndex("meaning")));

  }
}
