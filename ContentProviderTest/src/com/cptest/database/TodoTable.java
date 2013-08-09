package com.cptest.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class TodoTable
{
	
	// Database Table
	public static final String TABLE_TODO = "todos";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_SUMMARY = "summary";
	public static final String COLUMN_DESCRIPTION = "description";
	
	// SQL statement CREATE TABLE todo
	private static final String DATABASE_CREATE = "create table "
													+ TABLE_TODO
													+ " ( "
													+ COLUMN_ID + " integer primary key autoincrement, "
													+ COLUMN_CATEGORY + " text not null, "
													+ COLUMN_SUMMARY + " text not null, "
													+ COLUMN_DESCRIPTION + " text not null "
													+ " );";
	
	// create table todo
	public static void onCreate(SQLiteDatabase db)
	{
		Log.w(TodoTable.class.getName(), "will create table: " + TABLE_TODO);
		db.execSQL(DATABASE_CREATE);
	}
	
	// upgrade table todo
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(TodoTable.class.getName(), "will upgrade table: " + TABLE_TODO);
		db.execSQL("drop table if exists: " + TABLE_TODO);
		onCreate(db);
	}
}
