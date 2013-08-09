package com.cptest.contentprovider;

import java.util.Arrays;
import java.util.HashSet;
import android.util.Log;
import android.content.Context;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import android.text.TextUtils;

import com.cptest.database.TodoDatabaseHelper;
import com.cptest.database.TodoTable;

public class TodoContentProvider extends ContentProvider
{
	// database
	private TodoDatabaseHelper dbHelper;
	
	// UriMatcher
	private static final int TODOS = 10;
	private static final int TODO_ID = 20;
	
	private static final String AUTHORITY = "com.cptest.contentprovider";
	private static final String BASE_PATH = "todos";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/todos";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/todo";
	
	private static final UriMatcher  sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static 
	{
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, TODOS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TODO_ID);
	}
	
	@Override
	public boolean onCreate()
	{
		Log.w(TodoContentProvider.class.getName(), "will create content provider!");
		dbHelper = new TodoDatabaseHelper(getContext());
		Log.w(TodoContentProvider.class.getName(), "created content provider for database: " + dbHelper.getDatabaseName());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		// SQLiteQueryBuilder to build SQL query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		// Check if the caller has requested a column which does not exists
		checkColumns(projection);
		
		// Set the table
		queryBuilder.setTables(TodoTable.TABLE_TODO);
		
		int uriType = sURIMatcher.match(uri);
		switch(uriType)
		{
			case TODOS:
				break;
			case TODO_ID:
				// Adding the ID to the query
				queryBuilder.appendWhere(TodoTable.COLUMN_ID + "=" + uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Log.w(TodoContentProvider.class.getName(), "will query from table: " + queryBuilder.getTables());
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		// Set the URI to be watched 
		Log.w(TodoContentProvider.class.getName(), "will set the uri to be watched: " + uri);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}
	
	@Override
	public String getType(Uri uri)
	{
		return null;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = 0;
		
		int uriType = sURIMatcher.match(uri);
		switch(uriType)
		{
			case TODOS:
				Log.w(TodoContentProvider.class.getName(), "will insert item(" + values.toString() 
						+ ") into table " + TodoTable.TABLE_TODO);
				id = db.insert(TodoTable.TABLE_TODO, null, values);
				Log.w(TodoContentProvider.class.getName(), "inserted item id=" + id 
						+ " into table " + TodoTable.TABLE_TODO);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		// By default, CursorAdapter objects will get this notification.
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		int rowsDeleted = 0;
		int uriType = sURIMatcher.match(uri);
		switch(uriType)
		{
			case TODOS:
				Log.w(TodoContentProvider.class.getName(), "will delete item with selections(" + selection 
                        + "=" + Arrays.toString(selectionArgs) +  ") from database " + db.getPath());
				rowsDeleted = db.delete(TodoTable.TABLE_TODO, selection, selectionArgs);
				Log.w(TodoContentProvider.class.getName(), "deleted item id=" + rowsDeleted 
                        + " from database " + db.getPath());
				break;
			case TODO_ID:
				String requestedID = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection))
				{
					Log.w(TodoContentProvider.class.getName(), "will delete item id=" + requestedID 
                            + " from database " + db.getPath());
					rowsDeleted = db.delete(TodoTable.TABLE_TODO, TodoTable.COLUMN_ID + "=" + requestedID, null);
					Log.w(TodoContentProvider.class.getName(), "deleted item id=" + requestedID 
                            + " from database " + db.getPath());
				}
				else
				{
					Log.w(TodoContentProvider.class.getName(), "will delete item id=" + requestedID 
                            + " and selecitons(" + selection + ") from database " + db.getPath());
					rowsDeleted = db.delete(TodoTable.TABLE_TODO, 
                                            TodoTable.COLUMN_ID + "=" + requestedID + " and " + selection, selectionArgs);
					Log.w(TodoContentProvider.class.getName(), "deleted item id=" + rowsDeleted 
                            + " from database " + db.getPath());
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int rowsUpdated = 0;
		int uriType = sURIMatcher.match(uri);
		switch(uriType)
		{
			case TODOS:
				Log.w(TodoContentProvider.class.getName(), "will update item with selections(" + selection 
                        + ") from database " + db.getPath());
				rowsUpdated = db.update(TodoTable.TABLE_TODO, values, selection, selectionArgs);
				Log.w(TodoContentProvider.class.getName(), "updated item id=" + rowsUpdated 
                        + " from database " + db.getPath());
				break;
			case TODO_ID:
				String requestedID = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection))
				{
					Log.w(TodoContentProvider.class.getName(), "will update item id=" + requestedID 
                            + " from database " + db.getPath());
					rowsUpdated = db.update(TodoTable.TABLE_TODO, values, TodoTable.COLUMN_ID + "=" + requestedID, null);
					Log.w(TodoContentProvider.class.getName(), "updated item id=" + rowsUpdated 
                            + " from database " + db.getPath());
				}
				else
				{
					Log.w(TodoContentProvider.class.getName(), "will update item id=" + requestedID 
                            + " and selecitons(" + selection + ") from database " + db.getPath());
					rowsUpdated = db.update(TodoTable.TABLE_TODO, values, 
                                            TodoTable.COLUMN_ID + "=" + requestedID + " and " + selection, selectionArgs);
					Log.w(TodoContentProvider.class.getName(), "updated item id=" + rowsUpdated 
                            + " from database " + db.getPath());
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	private void checkColumns(String[] inputColumns)
	{
		String[] availableCols = { TodoTable.COLUMN_ID,
									TodoTable.COLUMN_CATEGORY, 
									TodoTable.COLUMN_DESCRIPTION,
									TodoTable.COLUMN_SUMMARY};
		if (inputColumns != null)
		{
			HashSet<String> requestedColsTemp = new HashSet<String> (Arrays.asList(inputColumns));
			HashSet<String> availableColsTemp = new HashSet<String> (Arrays.asList(availableCols));
			
			if(!availableColsTemp.containsAll(requestedColsTemp))
			{
				throw new IllegalArgumentException("Unknown requested columns in projection!");
			}
			
		}
	}
}
