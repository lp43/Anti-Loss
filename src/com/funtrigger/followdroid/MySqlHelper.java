package com.funtrigger.followdroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.funtrigger.followdroid.R;

public class MySqlHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME_KEYWORDS = "findphone";
    private static final String TABLE_CREATE_KEYWORDS =
                "CREATE TABLE " + TABLE_NAME_KEYWORDS + " (" +
//                "_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "_id INTEGER PRIMARY KEY," +
                "_value INTEGER);";


    public MySqlHelper(Context context){	
		super(context, DATABASE_NAME,null,DATABASE_VERSION);
	}
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL(TABLE_CREATE_KEYWORDS);
		 ContentValues cv1 = new ContentValues();
		 cv1.put("_value", "where is my phone?");
		 db.insert(TABLE_NAME_KEYWORDS, null, cv1);
		 
		 ContentValues cv2 = new ContentValues();
		 cv2.put("_value", "Wimp");
		 db.insert(TABLE_NAME_KEYWORDS, null, cv2);
		 
		 ContentValues cv3 = new ContentValues();
		 cv3.put("_value", "wimp");
		 db.insert(TABLE_NAME_KEYWORDS, null, cv3);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//oldVersion=舊的資料庫版本；newVersion=新的資料庫版本
		db.execSQL("DROP TABLE IF EXISTS config");
		onCreate(db);
	}
	
	public void insert(String value){
		SQLiteDatabase db = this.getWritableDatabase();
		//將新增的值放入ContentValues
		ContentValues cv = new ContentValues();
		cv.put("_value", value);
		db.insert(TABLE_NAME_KEYWORDS, null, cv);
	}
	
	public void delete(int id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME_KEYWORDS, new String("_id=?"), new String[]{Integer.toString(id)});
		
	}
	
	//查詢全部資料表，供Setting讀取用
	public Cursor getAll(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_KEYWORDS, new String[]{"_id","_value"}, null, null, null, null, null);
		
		return cursor;
		
	}
}
