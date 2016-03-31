package com.email.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.email.global.DatabaseGlobal;

/**
 * 
 * @author tangjiabing
 * 
 * @see 开源时间：2016年03月31日
 * 
 *      记得给我个star哦~
 * 
 */
public class DbOpenHelperUtil extends SQLiteOpenHelper {

	public DbOpenHelperUtil(Context context) {
		super(context, DatabaseGlobal.DATABASE_NAME, null,
				DatabaseGlobal.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder builder = new StringBuilder();
		builder.append("create table ").append(DatabaseGlobal.TABLE_EMAIL_OBJS)
				.append(" ( ").append(DatabaseGlobal.FIELD_ID)
				.append(" integer primary key autoincrement,")
				.append(DatabaseGlobal.FIELD_NAME).append(" text,")
				.append(DatabaseGlobal.FIELD_ADDRESS).append(" text,")
				.append(DatabaseGlobal.FIELD_OUT_KEY).append(" text")
				.append(" )");
		db.execSQL(builder.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
