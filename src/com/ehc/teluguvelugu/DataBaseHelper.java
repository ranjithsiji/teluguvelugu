package com.ehc.teluguvelugu;

//Here in code for use external database in android project. 
//1) first put your .db file(database file ) in assets folder
//2) then use below code for copy database into project package.
//package com.mobidhan.databasecopy;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static String database_n;// database name
	private static String database_p;// database path
	private static final int DATABASE_VERSION = 3;
	private static String DatabaseName;
	private SQLiteDatabase dataBase;
	private final Context dbContext;

	// DB_NAME is name of database which is in assets folder
	// path is our package name
	public DataBaseHelper(Context context, String DB_NAME, String PATH) {
		super(context, DB_NAME, null, DATABASE_VERSION);
		DatabaseName = DB_NAME;
		database_p = "/data/data/" + PATH + "/databases/";
		this.dbContext = context;
		database_n = DatabaseName;
		// checking database and open it if exists
		if (checkDataBase()) {
			openDataBase();
		} else {
			try {
				this.getReadableDatabase();
				copyDataBase();
				this.close();
				// openDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
			Toast.makeText(context, "", Toast.LENGTH_LONG).show();
		}
	}

	private void copyDataBase() throws IOException {
		InputStream myInput = dbContext.getAssets().open(database_n);
		String outFileName = database_p + database_n;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public SQLiteDatabase openDataBase() throws SQLException {
		String dbPath = database_p + database_n;
		dataBase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
		return dataBase;
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		boolean exist = false;
		try {
			String dbPath = database_p + database_n;
			checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			Log.v("santosh", "database error");
		}
		if (checkDB != null) {
			Log.v("santosh", "database exist");
			exist = true;
			checkDB.close();
		}
		return exist;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}