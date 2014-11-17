package com.legendleo.yeeshop.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "com.legendleo.yeekoor.db"; 
	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sqlCate1 = "create table tb_category1( _id integer primary key autoincrement, cateid1 integer, procate1 text, procate2 text );";
		db.execSQL(sqlCate1);
		
		String sqlCate2 = "create table tb_category2( _id integer primary key autoincrement, cateid1 integer, cateid2 integer, procate2 text, procatelink2 text );";
		db.execSQL(sqlCate2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
