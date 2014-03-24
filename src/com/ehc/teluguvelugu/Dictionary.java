package com.ehc.teluguvelugu;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class Dictionary {
    public SQLiteDatabase database;
    public Context context;

    public Dictionary(SQLiteDatabase database, Context context) {
        this.database = database;
        this.context = context;
    }

    public ArrayList dictionaryData() {
        ArrayList<String> words = new ArrayList<String>();
        Cursor data = database.rawQuery("Select * from eng2te order by eng_word", null);
        while (data.moveToNext()) {
            words.add(data.getString(data.getColumnIndex("eng_word")));
        }
        return words;
    }

    public Cursor dictionaryData(String matchString) {
        ArrayList<String> words = new ArrayList<String>();
        Cursor data = database.rawQuery("Select DISTINCT eng_word, rowid as _id from eng2te where eng_word like '" + matchString + "%'", null);
        return data;
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
            Cursor meaningOfWordComponentQuery = database.rawQuery("Select * from eng2te where eng_word='" + query + "'"
                    + "COLLATE NOCASE", null);
            boolean recordsExist = meaningOfWordComponentQuery.moveToFirst();
            if (recordsExist) {
                int index = meaningOfWordComponentQuery.getColumnIndex("meaning");
                String meaning = meaningOfWordComponentQuery.getString(index);
                return meaning;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void storeRecentWord(String word) {
        String meaning = getMeaning(word);
        if (meaning != null) {
            SharedPreferences storedRecents = getStoredPreferences("recent");
            Set recents = storedRecents.getStringSet("recentValues", new LinkedHashSet<String>());
            recents.add(word);
            SharedPreferences.Editor recentEditor = storedRecents.edit();
            recentEditor.putStringSet("recentValues", recents);
            recentEditor.commit();
        }
    }


    public SharedPreferences getStoredPreferences(String key) {
        return context.getSharedPreferences(key, 0);
    }


    public Set getFavourites() {
        SharedPreferences storedFavourites = getStoredPreferences("favourites");
        return storedFavourites.getStringSet("favourites", null);
    }


    public void storeFavouriteWord(String word) {
        SharedPreferences storedFavourites = getStoredPreferences("favourites");
        Set favourites = storedFavourites.getStringSet("favourites", new LinkedHashSet<String>());
        favourites.add(word);
        SharedPreferences.Editor favouriteEditor = storedFavourites.edit();
        favouriteEditor.putStringSet("favourites", favourites);
        favouriteEditor.commit();
        Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
    }

    public String getWordOfTheDay(String today) {
        SharedPreferences storedWordOfTheDay = getStoredPreferences("WORDOFDAY");
        return storedWordOfTheDay.getString(today, "");
    }


    public void saveWordOfTheDay(String date, String word) {
        SharedPreferences storedWordOfTheDay = getStoredPreferences("WORDOFDAY");
        SharedPreferences.Editor editor = storedWordOfTheDay.edit();
        editor.putString(date, word);
        editor.commit();
    }

    public Set getRecentWords() {
        SharedPreferences recentQueries = getStoredPreferences("recent");
        Set recents = recentQueries.getStringSet("recentValues", null);
        return recents;
    }
}
