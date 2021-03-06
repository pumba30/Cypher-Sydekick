// Class file for modifying the local database. Gives options to create users,
// delete users, delete all the users, retrieve all the users, and retrieve
// a users key by searching for their username. A SQL cursor function is used
// to search for different values in the table and retrieve results

package com.github.tbporter.cypher_sydekick.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UserKeyDOA {
	private SQLiteDatabase database;
	private UserKeyDatabaseHelper dbHelper;
	private String[] allColumns = { UserKeyDatabaseHelper.COLUMN_ID, UserKeyDatabaseHelper.COLUMN_USERNAME,
			UserKeyDatabaseHelper.COLUMN_KEY };
	
	public UserKeyDOA(Context context) {
		dbHelper = new UserKeyDatabaseHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public UserKey createUser(String user, String key) {
		ContentValues values = new ContentValues();
		values.put(UserKeyDatabaseHelper.COLUMN_USERNAME, user);
		values.put(UserKeyDatabaseHelper.COLUMN_KEY, key);
		long insertId = database.insert(UserKeyDatabaseHelper.TABLE_KEYS, null, values);
		Cursor cursor = database.query(UserKeyDatabaseHelper.TABLE_KEYS, allColumns,
				UserKeyDatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		UserKey newComment = cursorToData(cursor);
		cursor.close();
		return newComment;
	}
	
	public void deleteUser(String username) {
		database.delete(UserKeyDatabaseHelper.TABLE_KEYS, UserKeyDatabaseHelper.COLUMN_USERNAME + "=?", new String[]{username});
	}
	
	public void deleteAllUsers() {
		database.delete(UserKeyDatabaseHelper.TABLE_KEYS, null, null);
	}
	
	public ArrayList<String> getAllUsers() {
		ArrayList<String> usernames = new ArrayList<String>();
	
		Cursor cursor = database.query(UserKeyDatabaseHelper.TABLE_KEYS, allColumns,
				null, null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			UserKey username = cursorToData(cursor);
			usernames.add(username.getUsername());
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return usernames;
	}
	
	private UserKey cursorToData(Cursor cursor) {
		UserKey username = new UserKey();
		username.setId(cursor.getLong(0));
		username.setUsername(cursor.getString(1));
		username.setKey(cursor.getString(2));
		return username;
	}

	public String getKeyViaUsername(String username) {
		String newKey = null;

		Cursor cursor = database.query(UserKeyDatabaseHelper.TABLE_KEYS, null,
				"username=?", new String[]{username}, null, null, null);
		cursor.moveToFirst();
		UserKey cursordata = cursorToData(cursor);
		newKey = cursordata.getKey();
		cursor.close();

		return newKey;
	}
}
