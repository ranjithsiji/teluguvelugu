package com.ehc.teluguvelugu;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
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

public class DictionaryActivity extends Activity implements View.OnClickListener {
    public static Date date = new Date();
    public static String today;
    public static String query;
    public static SQLiteDatabase database;
    public static Typeface typeFacePothana;
    public static Typeface typeFaceOpenSans;
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
    public Dictionary dictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDatabase();
        setContentView(R.layout.activity_main);
        getWidgets();
        showWordOfTheDay();
        enableDeviceSearchButton();
        // giving functionality for autocomplete
        searchview.addTextChangedListener(getTextWatchListiner());
    }

    private TextWatcher getTextWatchListiner() {
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                pageTitleComponent.setVisibility(View.GONE);
                meaningOfWordComponent.setText("");
                String query = searchview.getText().toString();
                if (!query.equals("")) {
                    favouriteButton.setVisibility(View.VISIBLE);
                    meaning = dictionary.getMeaning(query);
                    if (meaning != null) {
                        renderWord(query, meaning);
                        dictionary.storeRecentWord(query);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        };
    }

    private void initializeDatabase() {
        DataBaseHelper helper = new DataBaseHelper(getApplicationContext(), "dictionary.sqlite", "com.ehc.teluguvelugu");
        database = helper.openDataBase();
        dictionary = new Dictionary(database, getApplicationContext());
    }

    private void enableDeviceSearchButton() {
        searchview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    query = searchview.getText().toString();
                    if (query.equals("")) {
                        meaningOfWordComponent.setText("Please Enter A Word");
                    } else {
                        dictionary.storeRecentWord(query);
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
                dictionary.storeFavouriteWord(searchview.getText().toString());
                break;
        }
    }

    // Getting query Of The Day
    public void showWordOfTheDay() {
        searchview.setVisibility(View.VISIBLE);
        pageTitleComponent.setVisibility(View.VISIBLE);
        pageTitleComponent.setText("Word Of The Day");
        favouriteButton.setVisibility(View.GONE);
        wordComponent.setVisibility(View.GONE);
        meaningOfWordComponent.setText("");
        today = date.getDate() + "";
        String wordOfTheDay = dictionary.getWordOfTheDay(today);
        if (wordOfTheDay.equals("")) {
            randomWord = dictionary.getRandomWord();
            dictionary.saveWordOfTheDay(today, randomWord);
            meaningOfWordComponent.setText(randomWord + "\n\n" + dictionary.getMeaning(randomWord));
        } else {
            meaningOfWordComponent.setText(wordOfTheDay + "\n\n" + dictionary.getMeaning(wordOfTheDay));
        }
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
        wordComponent.setVisibility(View.GONE);
        randomWord = dictionary.getRandomWord();
        searchview.setVisibility(View.GONE);
        favouriteButton.setVisibility(View.GONE);
        pageTitleComponent.setVisibility(View.VISIBLE);
        pageTitleComponent.setText("Random Word");
        searchview.clearComposingText();
        meaningOfWordComponent.setText(randomWord + "\n\n" + dictionary.getMeaning(randomWord));
    }

    private void showRecents() {
        pageTitleComponent.setVisibility(View.VISIBLE);
        pageTitleComponent.setText("Recently Searched querys");
        favouriteButton.setVisibility(View.GONE);
        meaningOfWordComponent.setVisibility(View.VISIBLE);
        searchview.setVisibility(View.VISIBLE);
        wordComponent.setVisibility(View.GONE);
        meaningOfWordComponent.setText("");
        searchview.clearComposingText();
        Set recents = dictionary.getRecentWords();
        if (recents != null) {
            Iterator<String> recentWords = recents.iterator();
            while (recentWords.hasNext()) {
                String nextWord = recentWords.next();
                Log.d("values", nextWord + recents.size());
                String word = nextWord + "\n" + dictionary.getMeaning(nextWord) + "\n\n";
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
        favouriteButton.setVisibility(View.GONE);
        favourites = dictionary.getFavourites();
        wordComponent.setVisibility(View.GONE);
        if (favourites != null) {
            Iterator<String> favouriteIterator = favourites.iterator();
            String viewFavouriteWords = "";
            while (favouriteIterator.hasNext()) {
                String favouriteWord = favouriteIterator.next();
                viewFavouriteWords = viewFavouriteWords + "\n\n" + favouriteWord + "\n" + dictionary.getMeaning(favouriteWord);
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
        favouriteButton.setVisibility(View.GONE);
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
        searchview = (AutoCompleteTextView) findViewById(R.id.searchView1);
//        searchview
//                .setAdapter(new ArrayAdapter<String>(this,
//                        android.R.layout.simple_dropdown_item_1line, dictionary.dictionaryData()));
//        searchview.setPadding(10, 0, 0, 0);
//        searchview.setThreshold(1);
//        searchview.setHint("English query");
    }

    private void renderWord(String query, String meaning) {
        wordComponent.setText(query);
        meaningOfWordComponent.setText(meaning);
    }
}
