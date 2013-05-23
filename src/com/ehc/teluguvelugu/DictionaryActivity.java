package com.ehc.teluguvelugu;

import java.util.ArrayList;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DictionaryActivity extends Activity implements View.OnClickListener {
	public static Date date = new Date();
	public static String today;
	public static String query;
	public static SQLiteDatabase database;
	public static Typeface typeFacePothana;
	public static Typeface typeFaceOpenSans;

	public SharedPreferences recentQueries;
	public SharedPreferences storedFavourites;
	public Set favourites;
	public String meaning;
	public String randomWord;
	public AutoCompleteTextView searchview;
	public ImageButton favouriteButton;
	public ArrayAdapter<String> adapter;
	public TextView pageTitleComponent;
	public TextView wordComponent;
	public TextView meaningOfWordComponent;
	public AssetManager assetmanager;
	public Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWidgets();
		showWordOfTheDay();
		enableDeviceSearchButton();

		// giving functionality for autocomplete
		searchview.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				pageTitleComponent.setVisibility(View.INVISIBLE);
				meaningOfWordComponent.setText("");
				query = searchview.getText().toString();
				if (!query.equals("")) {
					favouriteButton.setVisibility(View.VISIBLE);
					query = titleize(query);
					meaning = getMeaning(query);
					if (!meaning.equals("Sorry, we unable to find word")) {
						renderWord(query, meaning);
						storeRecentWord();
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}
		});
	}

	private void enableDeviceSearchButton() {
		// TODO Auto-generated method stub
		searchview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					query = searchview.getText().toString();
					if (query.equals("")) {
						meaningOfWordComponent.setText("Please Enter a query.");
					} else {
						storeRecentWord();
					}
					renderWord(query, meaning);

					return true;
				}
				return false;
			}
		});

	}

	// Giving Functionality to Search Button
	@Override
	public void onClick(View mainView) {
		switch (mainView.getId()) {
		case R.id.favourite:
			storeFavouriteWord();
			break;
		}
	}

	public void storeRecentWord() {
		query = titleize(query);
		meaning = getMeaning(query);
		if (!meaning.equals("Sorry, we unable to find word")) {
			recentQueries = getSharedPreferences("recent", 0);
			Set recents = recentQueries.getStringSet("recentValues", new LinkedHashSet<String>());
			recents.add(query);
			SharedPreferences.Editor recentEditor = recentQueries.edit();
			recentEditor.putStringSet("recentValues", recents);
			recentEditor.commit();
			favouriteButton.setVisibility(View.VISIBLE);
			pageTitleComponent.setVisibility(View.INVISIBLE);
		}
	}

	public void storeFavouriteWord() {
		storedFavourites = getSharedPreferences("favourites", 0);
		favourites = storedFavourites.getStringSet("favourites", new LinkedHashSet<String>());
		favourites.add(query);

		SharedPreferences.Editor favouriteEditor = storedFavourites.edit();
		favouriteEditor.putStringSet("favourites", favourites);
		favouriteEditor.commit();

		Toast.makeText(getApplicationContext(), "Added to favourites", Toast.LENGTH_SHORT).show();
	}

	// Converting given input according to Database Format
	public String titleize(String word) {
		char array[] = word.toCharArray();
		array[0] = Character.toUpperCase(word.charAt(0));

		for (int i = 1; i < array.length; i++) {
			array[i] = Character.toLowerCase(word.charAt(i));
		}

		String newWord = new String(array);
		Log.d("Modified Word", newWord);
		return newWord;
	}

	// Generating Random query from Database
	public String getRandomWord() {
		Cursor records = database.rawQuery(
				"select * from eng2te where rowid = (abs(random()) % (select max(rowid)+1 from eng2te))", null);
		records.moveToFirst();
		int word_index = records.getColumnIndex("eng_word");
		String word = records.getString(word_index);

		return word;
	}

	// Getting Meaning for Input query
	public String getMeaning(String query) {
		try {
			Cursor meaningOfWordComponentQuery = database.rawQuery("Select * from eng2te where eng_word='" + query + "'",
					null);
			boolean recordsExist = meaningOfWordComponentQuery.moveToFirst();

			if (recordsExist) {
				int index = meaningOfWordComponentQuery.getColumnIndex("meaning");
				String meaning = meaningOfWordComponentQuery.getString(index);
				Log.d(query, meaning);
				return meaning;
			} else {
				return "Sorry, we unable to find word";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Getting query Of The Day
	public void showWordOfTheDay() {
		searchview.setVisibility(View.VISIBLE);
		pageTitleComponent.setVisibility(View.VISIBLE);
		pageTitleComponent.setText("Word Of The Day");
		favouriteButton.setVisibility(View.INVISIBLE);
		wordComponent.setVisibility(View.INVISIBLE);
		meaningOfWordComponent.setText("");
		today = date.getDate() + "";
		Log.d("Todays Date", today);

		SharedPreferences sharedPreference = getSharedPreferences("WORDOFDAY", 0);
		String wordOfTheDay = sharedPreference.getString(today, "");

		if (wordOfTheDay.equals("")) {
			randomWord = getRandomWord();
			SharedPreferences.Editor editor = sharedPreference.edit();
			editor.putString(today, randomWord);
			editor.commit();
			meaningOfWordComponent.setText(randomWord + "\n\n" + getMeaning(randomWord));
		} else {
			meaningOfWordComponent.setText(wordOfTheDay + "\n\n" + getMeaning(wordOfTheDay));
		}
	}

	// Test method to log all the recent querys searched
	public Set getRecentWords() {
		recentQueries = getSharedPreferences("recent", 0);
		Set recents = recentQueries.getStringSet("recentValues", null);
		return recents;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menuitems, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.home:
			showWordOfTheDay();
			break;
		case R.id.favourites:
			showFavourites();
			break;
		case R.id.aboutus:
			showAboutUs();
			break;
		case R.id.recent:
			showRecents();
			break;
		case R.id.random:
			showRandom();
			break;
		}
		return true;
	}

	private void showRandom() {
		wordComponent.setVisibility(View.INVISIBLE);
		randomWord = getRandomWord();
		searchview.setVisibility(View.VISIBLE);
		favouriteButton.setVisibility(View.INVISIBLE);
		pageTitleComponent.setVisibility(View.VISIBLE);
		pageTitleComponent.setText("Random Word");
		meaningOfWordComponent.setText(randomWord + "\n\n" + getMeaning(randomWord));
	}

	private void showRecents() {
		pageTitleComponent.setVisibility(View.VISIBLE);
		pageTitleComponent.setText("Recently Searched querys");
		favouriteButton.setVisibility(View.INVISIBLE);
		meaningOfWordComponent.setVisibility(View.VISIBLE);
		searchview.setVisibility(View.VISIBLE);
		wordComponent.setVisibility(View.INVISIBLE);
		meaningOfWordComponent.setText("");

		Set recents = getRecentWords();

		if (recents != null) {
			Iterator<String> recentWords = recents.iterator();
			String recentlyViewedQueries = "";

			while (recentWords.hasNext()) {
				String nextWord = recentWords.next();
				Log.d("values", nextWord + recents.size());
				String word = nextWord + "\n" + getMeaning(nextWord) + "\n\n";
				meaningOfWordComponent.append(word);
			}
		} else {
			meaningOfWordComponent.setText("No Recents Found");
		}
	}

	private void showFavourites() {
		searchview.setVisibility(View.VISIBLE);
		pageTitleComponent.setVisibility(View.VISIBLE);
		meaningOfWordComponent.setVisibility(View.VISIBLE);
		pageTitleComponent.setText("Your Favourites");
		favouriteButton.setVisibility(View.INVISIBLE);
		storedFavourites = getSharedPreferences("favourites", 0);
		favourites = storedFavourites.getStringSet("favourites", null);
		wordComponent.setVisibility(View.INVISIBLE);

		if (favourites != null) {
			Iterator<String> favouriteIterator = favourites.iterator();
			String viewFavouriteWords = "";

			while (favouriteIterator.hasNext()) {
				String favouriteWord = favouriteIterator.next();
				Log.d("values", favouriteWord + favourites.size());
				viewFavouriteWords = viewFavouriteWords + "\n\n" + favouriteWord + "\n" + getMeaning(favouriteWord);
			}
			meaningOfWordComponent.setText(viewFavouriteWords);
		} else {
			meaningOfWordComponent.setText("No Favourites Found");
		}
	}

	private void showAboutUs() {
		searchview.setVisibility(View.GONE);
		wordComponent.setVisibility(View.GONE);
		pageTitleComponent.setText("About Us");
		String aboutus = "We are a personalized technology consulting firm specialized in building large scale web & mobile applications using cutting edge technologies.\n \n Helping clients build better software systems is the core of our business.Let us help you realize the next big idea.";
		meaningOfWordComponent.setText(aboutus);
	}

	public void getWidgets() {
		favouriteButton = (ImageButton) findViewById(R.id.favourite);
		pageTitleComponent = (TextView) findViewById(R.id.wordoftheday);
		favouriteButton.setVisibility(View.INVISIBLE);
		favouriteButton.setOnClickListener(this);
		meaningOfWordComponent = (TextView) findViewById(R.id.meaning);
		wordComponent = (TextView) findViewById(R.id.word);
		meaningOfWordComponent.setMovementMethod(new ScrollingMovementMethod());

		context = getBaseContext();
		assetmanager = getAssets();

		typeFacePothana = Typeface.createFromAsset(assetmanager, "Pothana2000.ttf");
		typeFaceOpenSans = Typeface.createFromAsset(assetmanager, "OpenSans_Semibold.ttf");

		meaningOfWordComponent.setTypeface(typeFacePothana);
		pageTitleComponent.setTypeface(typeFaceOpenSans);

		DataBaseCopy dbcopy = new DataBaseCopy(context, "dictionary.sqlite", "com.ehc.teluguvelugu");

		database = dbcopy.openDataBase();
		searchview = (AutoCompleteTextView) findViewById(R.id.searchView1);

		// searchview
		// .setAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_dropdown_item_1line, dictionaryData()));
		searchview.setPadding(10, 0, 0, 0);
		searchview.setThreshold(1);
		searchview.setHint("English query");
	}

	public ArrayList dictionaryData() {
		ArrayList<String> words = new ArrayList<String>();

		Cursor data = database.rawQuery("Select * from eng2te order by eng_word", null);

		while (data.moveToNext()) {
			words.add(data.getString(data.getColumnIndex("eng_word")));
		}

		return words;
	}

	private void renderWord(String query, String meaning) {
		// TODO Auto-generated method stub
		wordComponent.setText(query);
		meaningOfWordComponent.setText(meaning);
	}
}
