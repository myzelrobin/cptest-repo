package com.cptest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TodoDatabaseHelper extends SQLiteOpenHelper
{

	private static final String DATABASE_NAME = "todos.db";
	private static final int DATABASE_VERSION = 2;
	
	public TodoDatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	//create database
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.w(TodoTable.class.getName(), "will create database");
		TodoTable.onCreate(db);
	}
	
	//upgrade database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(TodoTable.class.getName(), "will upgrade database from version " + oldVersion + " to " + newVersion 
			  + ", which will destroy all old data");
		TodoTable.onUpgrade(db, oldVersion, newVersion);
	}
}
