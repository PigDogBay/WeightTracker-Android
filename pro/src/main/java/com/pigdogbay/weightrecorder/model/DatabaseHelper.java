package com.pigdogbay.weightrecorder.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper implements IReadingsDatabase
{
	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "_id";
	private static final String KEY_WEIGHT_COLUMN = "weight";
	private static final String KEY_DATE_COLUMN = "date";
	private static final String KEY_COMMENT_COLUMN = "comment";
	private static final int INDEX_WEIGHT = 1;
	private static final int INDEX_DATE = 2;
	private static final int INDEX_COMMENT = 3;

	private static final String DATABASE_NAME = "weight_recorder.db";
	public static final String DATABASE_TABLE = "readings";
	private static final int DATABASE_VERSION = 1;

	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_WEIGHT_COLUMN
			+ " float, " + KEY_DATE_COLUMN + " integer, " + KEY_COMMENT_COLUMN
			+ " text);";

	private static final String COUNT_QUERY = "SELECT  * FROM "
			+ DATABASE_TABLE;

	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Called when no database exists in disk and the helper class needs
	// to create a new one.
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DATABASE_CREATE);
	}
	
	// Called when there is a database version mismatch meaning that
	// the version of the database on disk needs to be upgraded to
	// the current version.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if (newVersion > oldVersion)
		{
			// The simplest case is to drop the old table and create a new one.
			db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
			// Create a new one.
			onCreate(db);
		}
	}

	/* (non-Javadoc)
	 * @see com.pigdogbay.weightrecorder.model.IReadingsDatabase#addReading(com.pigdogbay.weightrecorder.model.Reading)
	 */
	@Override
	public void addReading(Reading reading)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		insert(db, reading);
	}
	/* (non-Javadoc)
	 * @see com.pigdogbay.weightrecorder.model.IReadingsDatabase#addReadings(java.util.List)
	 */
	@Override
	public void addReadings(List<Reading> readings)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		try
		{
			db.beginTransaction();
			for (Reading r : readings)
			{
				insert(db, r);
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
		
	}

	@Override
	public void mergeReadings(List<Reading> readings)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		try
		{
			db.beginTransaction();
			for (Reading r : readings)
			{
				if (r._ID==0)
				{
					insert(db, r);
				}
				else
				{
					update(db,r);
				}
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
		
	}
	private void insert(SQLiteDatabase db, Reading reading)
	{
		ContentValues values = new ContentValues();
		values.put(KEY_WEIGHT_COLUMN, reading.getWeight());
		values.put(KEY_DATE_COLUMN, reading.getDate().getTime());
		values.put(KEY_COMMENT_COLUMN, reading.getComment());
		db.insert(DATABASE_TABLE, null, values);
	}
	private void update(SQLiteDatabase db, Reading reading)
	{
		ContentValues values = new ContentValues();
		values.put(KEY_WEIGHT_COLUMN, reading.getWeight());
		values.put(KEY_DATE_COLUMN, reading.getDate().getTime());
		values.put(KEY_COMMENT_COLUMN, reading.getComment());
		String where = KEY_ID + " = " + String.valueOf(reading.getID());
		db.update(DATABASE_TABLE, values, where, null);
	}

	/* (non-Javadoc)
	 * @see com.pigdogbay.weightrecorder.model.IReadingsDatabase#getReading(int)
	 */
	@Override
	public Reading getReading(int id)
	{
		String[] result_columns = new String[] { KEY_ID, KEY_WEIGHT_COLUMN,
				KEY_DATE_COLUMN, KEY_COMMENT_COLUMN };
		String where = KEY_ID + " = " + String.valueOf(id);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(DATABASE_TABLE, result_columns, where, null,
				null, null, null, null);
		Reading reading = null;
		if (cursor.moveToFirst())
		{
			reading = new Reading(id, cursor.getFloat(INDEX_WEIGHT), new Date(
					cursor.getLong(INDEX_DATE)),
					cursor.getString(INDEX_COMMENT));
			cursor.close();
		}
		return reading;
	}

	/* (non-Javadoc)
	 * @see com.pigdogbay.weightrecorder.model.IReadingsDatabase#getAllReadings()
	 */
	@Override
	public List<Reading> getAllReadings()
	{
		List<Reading> list = new ArrayList<Reading>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(COUNT_QUERY, null);
		if (cursor.moveToFirst())
		{
			do
			{
				Reading reading = new Reading(cursor.getInt(0),
						cursor.getFloat(INDEX_WEIGHT), new Date(
								cursor.getLong(INDEX_DATE)),
						cursor.getString(INDEX_COMMENT));
				list.add(reading);

			} while (cursor.moveToNext());
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see com.pigdogbay.weightrecorder.model.IReadingsDatabase#deleteReading(com.pigdogbay.weightrecorder.model.Reading)
	 */
	@Override
	public void deleteReading(Reading reading)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String where = KEY_ID + " = " + String.valueOf(reading.getID());
		db.delete(DATABASE_TABLE, where, null);
	}

	/* (non-Javadoc)
	 * @see com.pigdogbay.weightrecorder.model.IReadingsDatabase#getReadingsCount()
	 */
	@Override
	public int getReadingsCount()
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(COUNT_QUERY, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	/* (non-Javadoc)
	 * @see com.pigdogbay.weightrecorder.model.IReadingsDatabase#updateReading(com.pigdogbay.weightrecorder.model.Reading)
	 */
	@Override
	public int updateReading(Reading reading)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_WEIGHT_COLUMN, reading.getWeight());
		values.put(KEY_DATE_COLUMN, reading.getDate().getTime());
		values.put(KEY_COMMENT_COLUMN, reading.getComment());
		String where = KEY_ID + " = " + String.valueOf(reading.getID());
		return db.update(DATABASE_TABLE, values, where, null);
	}
	
	/* (non-Javadoc)
	 * @see com.pigdogbay.weightrecorder.model.IReadingsDatabase#deleteAllReadings()
	 */
	@Override
	public void deleteAllReadings()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DATABASE_TABLE, null, null);
	}
	

}
