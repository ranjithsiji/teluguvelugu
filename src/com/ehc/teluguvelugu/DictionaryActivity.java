package com.ehc.teluguvelugu;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.widget.*;

public class DictionaryActivity extends Activity implements View.OnClickListener {
    public static Date date = new Date();
    public static String today;
    public static SQLiteDatabase database;
    public static Typeface typeFacePothana;
    public static Typeface typeFaceOpenSans;
    public Set favourites;
    public String meaning;
    public String randomWord;
    public ImageButton favouriteButton;
    public TextView pageTitleComponent;
    public TextView meaningOfWordComponent;
    public AssetManager assetmanager;
    public Context context;
    public Dictionary dictionary;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        initializeDatabase();
        setContentView(R.layout.activity_main);
        getWidgets();
        showWordOfTheDay();
    }

    private void initializeDatabase() {
        DataBaseHelper helper = new DataBaseHelper(getApplicationContext(), "dictionary.sqlite", "com.ehc.teluguvelugu");
        database = helper.openDataBase();
        dictionary = new Dictionary(database, getApplicationContext());
    }


    @Override
    public void onClick(View mainView) {
        switch (mainView.getId()) {
            case R.id.favourite:
                dictionary.storeFavouriteWord(mSearchView.getQuery().toString());
                break;
        }
    }

    public void showWordOfTheDay() {
        pageTitleComponent.setVisibility(View.VISIBLE);
        pageTitleComponent.setText("Word Of The Day");
        favouriteButton.setVisibility(View.GONE);
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
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);
        return true;
    }

    private void setupSearchView(MenuItem searchItem) {
        searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchView.setOnQueryTextListener(getListener());
    }

    private SearchView.OnQueryTextListener getListener() {
        return new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() >= 3) {
                    mSearchView.setSuggestionsAdapter(getCursorAdapter(newText));
                }
                return false;
            }

            public boolean onQueryTextSubmit(String query) {
                pageTitleComponent.setVisibility(View.GONE);
                meaningOfWordComponent.setText("");
                if (!query.equals("")) {
                    mSearchView.clearFocus();
                    favouriteButton.setVisibility(View.VISIBLE);
                    meaning = dictionary.getMeaning(query);
                    if (meaning != null) {
                        renderWord(query, meaning);
                        dictionary.storeRecentWord(query);
                    } else {
                        favouriteButton.setVisibility(View.GONE);
                        meaningOfWordComponent.setText("Sorry! Couldn't find meaning");
                    }
                }
                return true;
            }

        };
    }

    private CursorAdapter getCursorAdapter(String text) {

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1, dictionary.dictionaryData(text), new String[]{"eng_word"},
                new int[]{android.R.id.text1}, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSearchView.setQuery(text.getText(), true);
                    }
                });
                return view;
            }
        };
        return cursorAdapter;
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
        randomWord = dictionary.getRandomWord();
        favouriteButton.setVisibility(View.GONE);
        pageTitleComponent.setVisibility(View.VISIBLE);
        pageTitleComponent.setText("Random Word");
        meaningOfWordComponent.setText(randomWord + "\n\n" + dictionary.getMeaning(randomWord));
    }

    private void showRecents() {
        pageTitleComponent.setVisibility(View.VISIBLE);
        pageTitleComponent.setText("Recently Searched querys");
        favouriteButton.setVisibility(View.GONE);
        meaningOfWordComponent.setVisibility(View.VISIBLE);
        meaningOfWordComponent.setText("");
        Set recents = dictionary.getRecentWords();
        if (recents != null) {
            Iterator<String> recentWords = recents.iterator();
            while (recentWords.hasNext()) {
                String nextWord = recentWords.next();
                String word = nextWord + "\n" + dictionary.getMeaning(nextWord) + "\n\n";
                meaningOfWordComponent.append(word);
            }
        } else {
            meaningOfWordComponent.setText("No Recents Found");
        }
    }

    private void showFavourites() {
        pageTitleComponent.setVisibility(View.VISIBLE);
        meaningOfWordComponent.setVisibility(View.VISIBLE);
        pageTitleComponent.setText("Your Favourites");
        favouriteButton.setVisibility(View.GONE);
        favourites = dictionary.getFavourites();
        if (favourites != null) {
            Iterator<String> favouriteIterator = favourites.iterator();
            String viewFavouriteWords = "";
            while (favouriteIterator.hasNext()) {
                String favouriteWord = favouriteIterator.next();
                viewFavouriteWords = viewFavouriteWords + favouriteWord + "\n" + dictionary.getMeaning(favouriteWord) + "\n\n";
            }
            meaningOfWordComponent.setText(viewFavouriteWords);
        } else {
            meaningOfWordComponent.setText("No Favourites Found");
        }
    }

    private void showAboutUs() {
        pageTitleComponent.setText("About Us");
        String aboutus = getResources().getString(R.string.about);
        meaningOfWordComponent.setText(aboutus);
    }

    public void getWidgets() {
        favouriteButton = (ImageButton) findViewById(R.id.favourite);
        pageTitleComponent = (TextView) findViewById(R.id.page_title);
        favouriteButton.setVisibility(View.GONE);
        favouriteButton.setOnClickListener(this);
        meaningOfWordComponent = (TextView) findViewById(R.id.meaning);
        meaningOfWordComponent.setMovementMethod(new ScrollingMovementMethod());
        context = getBaseContext();
        assetmanager = getAssets();
        typeFacePothana = Typeface.createFromAsset(assetmanager, "Pothana2000.ttf");
        typeFaceOpenSans = Typeface.createFromAsset(assetmanager, "OpenSans_Semibold.ttf");
        meaningOfWordComponent.setTypeface(typeFacePothana);
        pageTitleComponent.setTypeface(typeFaceOpenSans);
    }

    private void renderWord(String query, String meaning) {
        meaningOfWordComponent.setText(query + "\n\n" + meaning);
    }
}
