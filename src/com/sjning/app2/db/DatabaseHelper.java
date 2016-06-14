package com.sjning.app2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int VERSION = 2;
	// 本地数据库的名字
	public static final String DATA_NAME = "sms_data.db";// 聊天记录信息表
	// 巡检项表
	public static final String TABLE_SMS = "table_sms";

	// 巡检项类型表

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public DatabaseHelper(Context context) {
		this(context, DATA_NAME, VERSION);
	}

	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sqlStr1 = "create table "
				+ TABLE_SMS
				+ "(phone varchar(20) primary key, dateTime varchar(30) ,body text, items text)";
		db.execSQL(sqlStr1);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("DatabaseHelper onUpgrade");
	}
}
