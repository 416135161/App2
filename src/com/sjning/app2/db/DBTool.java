package com.sjning.app2.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sjning.app2.receive.MessageItem;

public class DBTool {
	private static DBTool dbTool;

	private DBTool() {

	}

	public static DBTool getInstance() {
		if (dbTool == null)
			dbTool = new DBTool();
		return dbTool;

	}

	public synchronized List<MessageItem> getSavedMessage(Context context,
			String searchStr, String phone) {
		List<MessageItem> list = null;

		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

		String sql = "SELECT * FROM table_sms WHERE date(dateTime) > strftime( '%Y-%m-%d %H:%M:%S', date('now', '-1 month')) ";
		if (searchStr != null) {
			sql += "and (body like '%" + searchStr + "%') ";
		}
		if (phone != null && !phone.equals(""))
			sql += "and phone = " + phone;
		sql += " ORDER BY dateTime DESC";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				if (list == null) {
					list = new ArrayList<MessageItem>();
				}
				MessageItem message = new MessageItem();
				message.setDate(cursor.getString(cursor
						.getColumnIndex("dateTime")));
				message.setBody(cursor.getString(cursor.getColumnIndex("body")));
				message.setPhone(cursor.getString(cursor
						.getColumnIndex("phone")));
				message.setItems(cursor.getString(cursor
						.getColumnIndex("items")));
				list.add(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return list;
	}

	public synchronized void saveMessage(Context context, MessageItem item) {
		if (item == null)
			return;
		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.clear();
		contentValues.put("dateTime", item.getDate());
		contentValues.put("phone", item.getPhone());
		contentValues.put("body", item.getBody());
		contentValues.put("items", item.getItems());
		db.replace(DatabaseHelper.TABLE_SMS, null, contentValues);

		if (item.getChildItems() != null) {
			for (String childItem : item.getChildItems()) {
				contentValues.clear();
				contentValues.put("phone", item.getPhone());
				contentValues.put("childItem", childItem);
				db.replace(DatabaseHelper.TABLE_SMS_CHILD, null, contentValues);
			}

		}

		db.close();
	}
}
