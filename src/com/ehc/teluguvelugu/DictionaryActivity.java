package com.ehc.teluguvelugu;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

public class DictionaryActivity extends Activity {
	public static Date date = new Date();
	public static String day;
	public static String word;
	public static SQLiteDatabase database;
	public static Typeface typeFace;
	public TextView result;
	public SharedPreferences recentWords;
	public Set recent;
	private Object recentlyViewedWords;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button search = (Button) findViewById(R.id.search);
		Button random = (Button) findViewById(R.id.random);
		final SearchView searchview = (SearchView) findViewById(R.id.searchView1);
		result = (TextView) findViewById(R.id.meaning);
		final Context context = getBaseContext();
		AssetManager assetmanager = getAssets();
		typeFace = Typeface.createFromAsset(assetmanager, "Pothana2000.ttf");
		result.setTypeface(typeFace);
		DataBaseCopy dbcopy = new DataBaseCopy(context, "dictionary.sqlite", "com.ehc.teluguvelugu");
		database = dbcopy.openDataBase();
		// result.setText("Word Of The Day:\n" + getWordOfDay());
		showWordOfDay();

		// Giving Functionality to Search Button
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String word = searchview.getQuery().toString();
				if (word.equals("")) {
					result.setText("Please Enter a word.");
				} else {
					word = inputConversion(word);
					recentWords = getSharedPreferences("recent", 0);
					recent = recentWords.getStringSet("recentValues", new LinkedHashSet<String>());
					recent.add(word);
					SharedPreferences.Editor recentEditor = recentWords.edit();
					recentEditor.putStringSet("recentValues", recent);
					recentEditor.commit();
					String meaning = getMeaning(word);
					result.setText(meaning);
				}
			}

		});
		// Giving Functionality to Random Button
		random.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String randomword = getRandomWord();
				result.setText("Random Word:\n" + randomword);
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences sharedPreference = getSharedPreferences("WORDOFDAY", 0);
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(day, word);
		editor.commit();

	}

	// Converting given input according to Database Format
	public String inputConversion(String input) {
		char array[] = input.toCharArray();
		array[0] = Character.toUpperCase(input.charAt(0));
		for (int i = 1; i < array.length; i++) {
			array[i] = Character.toLowerCase(input.charAt(i));
		}
		String changedword = new String(array);
		Log.d("change word", changedword);
		return changedword;
	}

	// Generating Random Word from Database
	public String getRandomWord() {
		Cursor result_query = database.rawQuery(
				"select * from eng2te where rowid = (abs(random()) % (select max(rowid)+1 from eng2te))", null);
		result_query.moveToFirst();
		Log.d(result_query.getString(result_query.getColumnIndex("eng_word")),
				result_query.getString(result_query.getColumnIndex("meaning")));
		return result_query.getString(result_query.getColumnIndex("eng_word")) + " = "
				+ result_query.getString(result_query.getColumnIndex("meaning"));
	}

	// Getting Meaning for Input Word
	public String getMeaning(String word) {
		try {
			Cursor result_query = database.rawQuery("Select * from eng2te where eng_word='" + word + "'", null);
			if (result_query.moveToFirst()) {
				Log.d(word, result_query.getString(result_query.getColumnIndex("meaning")));
				return result_query.getString(result_query.getColumnIndex("meaning"));
			} else {
				return "Sorry, we unable to find word";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Getting Word Of The Day
	public void showWordOfDay() {
		day = date.getDate() + "";
		Log.d("dateeeeeeeeeeeeeeeeeeeeeeee", day);
		SharedPreferences sharedPreference = getSharedPreferences("WORDOFDAY", 0);
		String word_day = sharedPreference.getString(day, "");
		if (word_day.equals("")) {
			word = getRandomWord();
			result.setText("Word Of The Day:\n" + word);
		} else {
			result.setText("Word Of The Day:\n" + word_day);
		}
	}

	// Test method to log all the recent words searched
	private void recentWord() {
		recentWords = getSharedPreferences("recent", 0);
		recent = recentWords.getStringSet("recentValues", null);
		Iterator<String> setIterator = recent.iterator();
		String recentlyViewedWords = "";
		while (setIterator.hasNext()) {
			String s = setIterator.next();
			Log.d("values", s + recent.size());
			recentlyViewedWords = recentlyViewedWords + "\n" + s;
		}
		result.setText("Recently searched Words:\n" + recentlyViewedWords);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menuitems, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.home:
			showWordOfDay();
			break;
		case R.id.favourites:
			showFavourites();
			break;
		case R.id.aboutus:
			showAboutUs();
			break;
		case R.id.recent:
			showRecent();
			break;
		}

		return true;
	}

	private void showRecent() {
		recentWord();
	}

	private void showFavourites() {

	}

	private void showAboutUs() {

		String aboutus = "1).We are a personalized technology consulting firm specialized in building large scale web & mobile applications using cutting edge technologies.\n2).Helping clients build better software systems is the core of our business.Let us help you realize the next big idea.";
		result.setText(aboutus);

	}

}
