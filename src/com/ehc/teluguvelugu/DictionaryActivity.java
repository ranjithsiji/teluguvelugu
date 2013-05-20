package com.ehc.teluguvelugu;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class DictionaryActivity extends Activity implements View.OnClickListener {
	public static Date date = new Date();
	public static String day;
	public static String word;
	public static SQLiteDatabase database;
	public static Typeface typeFace;
	public TextView result;
	public SharedPreferences recentWords;
	public SharedPreferences favouriteWords;
	public Set favourite;
	public Set recent;
	private Object recentlyViewedWords;
	public String meaning;
	public String randomword;
	public Button search;
	public AutoCompleteTextView searchview;
	public ImageButton favourites;
	public ArrayList<String> matchingWordList = new ArrayList<String>();
	public ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		search = (Button) findViewById(R.id.search);
		search.setOnClickListener(this);
		favourites = (ImageButton) findViewById(R.id.favourite);
		favourites.setOnClickListener(this);
		// searchview = (SearchView) findViewById(R.id.searchView1);
		result = (TextView) findViewById(R.id.meaning);
		final Context context = getBaseContext();
		AssetManager assetmanager = getAssets();
		typeFace = Typeface.createFromAsset(assetmanager, "Pothana2000.ttf");
		result.setTypeface(typeFace);
		DataBaseCopy dbcopy = new DataBaseCopy(context, "dictionary.sqlite", "com.ehc.teluguvelugu");
		database = dbcopy.openDataBase();
		showWordOfDay();
		searchview = (AutoCompleteTextView) findViewById(R.id.searchView1);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, matchingWordList);
		searchview.setAdapter(adapter);
		searchview.setHint("English Word");
		// giving functionality for autocomplete
		searchview.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String word = searchview.getText().toString();
				if (!word.equals("")) {
					Log.d("arrayLiStttttttt", matchingWordList.toString());
					word = inputConversion(word);
					Cursor data = database.rawQuery("Select * from eng2te where eng_word like'" + word + "%" + "'", null);
					if (data.moveToFirst()) {
						do {
							matchingWordList.add(data.getString(data.getColumnIndex("eng_word")));
						} while (data.moveToNext());
					}
				} else {
					matchingWordList.removeAll(matchingWordList);
				}
			}
		});
	}

	// Giving Functionality to Search Button
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search:
			word = searchview.getText().toString();
			if (word.equals("")) {
				result.setText("Please Enter a word.");
			} else {
				word = inputConversion(word);
				meaning = getMeaning(word);
				if (!meaning.equals("Sorry, we unable to find word")) {
					recentWords = getSharedPreferences("recent", 0);
					recent = recentWords.getStringSet("recentValues", new LinkedHashSet<String>());
					recent.add(word);
					SharedPreferences.Editor recentEditor = recentWords.edit();
					recentEditor.putStringSet("recentValues", recent);
					recentEditor.commit();
				}
				result.setText(meaning);
			}
			break;
		case R.id.favourite:
			if (!meaning.equals("Sorry, we unable to find word")) {
				favouriteWords = getSharedPreferences("favourites", 0);
				favourite = favouriteWords.getStringSet("favourites", new LinkedHashSet<String>());
				favourite.add(word);
				SharedPreferences.Editor favouriteEditor = favouriteWords.edit();
				favouriteEditor.putStringSet("favourites", favourite);
				favouriteEditor.commit();
			}
			break;
		}
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
			recentlyViewedWords = recentlyViewedWords + "\n" + s + "=" + getMeaning(s);
		}
		result.setText("Recently searched Words:\n" + recentlyViewedWords);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menuitems, menu);
		setMenuBackground();
		return true;
	}

	@SuppressLint("ResourceAsColor")
	protected void setMenuBackground() {
		getLayoutInflater().setFactory(new Factory() {
			@Override
			public View onCreateView(String name, Context context, AttributeSet attrs) {
				if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
					try {
						LayoutInflater f = getLayoutInflater();
						final View view = f.createView(name, null, attrs);
						/*
						 * The background gets refreshed each time a new item is added the
						 * options menu. So each time Android applies the default background
						 * we need to set our own background. This is done using a thread
						 * giving the background change as runnable object
						 */
						new Handler().post(new Runnable() {
							@Override
							public void run() {
								// sets the background color
								view.setBackgroundColor(R.color.sysGreen);
								// sets the text color
								((TextView) view).setTextColor(Color.BLACK);
								// sets the text size
								((TextView) view).setTextSize(18);
							}
						});
						return view;
					} catch (InflateException e) {
					} catch (ClassNotFoundException e) {
					}
				}
				return null;
			}
		});
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
		case R.id.random:
			randomword = getRandomWord();
			result.setText("Random Word:\n" + randomword);
			break;
		}
		return true;
	}

	private void showRecent() {
		recentWord();
	}

	private void showFavourites() {
		favouriteWords = getSharedPreferences("favourites", 0);
		favourite = favouriteWords.getStringSet("favourites", null);
		Iterator<String> favouriteIterator = favourite.iterator();
		String viewFavouriteWords = "";
		while (favouriteIterator.hasNext()) {
			String s = favouriteIterator.next();
			Log.d("values", s + favourite.size());
			viewFavouriteWords = viewFavouriteWords + "\n" + s + "=" + getMeaning(s);
		}
		result.setText("Favourites :\n" + viewFavouriteWords);
	}

	private void showAboutUs() {
		String aboutus = "1).We are a personalized technology consulting firm specialized in building large scale web & mobile applications using cutting edge technologies.\n2).Helping clients build better software systems is the core of our business.Let us help you realize the next big idea.";
		result.setText(aboutus);
	}
}
