package com.email.util;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author tangjiabing
 * 
 * @see 开源时间：2016年03月31日
 * 
 *      记得给我个star哦~
 * 
 */
public class DbUtil {

	private static DbUtil mInstance;
	private DbOpenHelperUtil mDbHelperUtil;
	private SQLiteDatabase mSQLiteDb;

	private DbUtil(Context context) {
		mDbHelperUtil = new DbOpenHelperUtil(context);
		mSQLiteDb = mDbHelperUtil.getWritableDatabase();
	}

	// **********************************************************
	// 公有方法

	public synchronized static DbUtil getInstance(Context context) {
		if (mInstance == null)
			mInstance = new DbUtil(context);
		return mInstance;
	}

	public synchronized static void closeDb() {
		if (mInstance != null)
			mInstance.close();
		mInstance = null;
	}

	public boolean insertMult(String tableName, String[] fieldNames,
			ArrayList<Object[]> dataList) {
		mSQLiteDb.beginTransaction();
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("insert into ").append(tableName).append("(");
			for (String field : fieldNames)
				builder.append(field).append(",");
			builder.replace(builder.length() - 1, builder.length(), ") values(");
			for (int i = 0; i < fieldNames.length; i++)
				builder.append("?,");
			builder.replace(builder.length() - 1, builder.length(), ")");
			String sql = builder.toString();
			for (Object[] bindArgs : dataList)
				mSQLiteDb.execSQL(sql, bindArgs);
			mSQLiteDb.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			mSQLiteDb.endTransaction();
		}
	}

	public boolean delete(String tableName) {
		try {
			String sql = "delete from " + tableName;
			mSQLiteDb.execSQL(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Cursor queryByLike(String tableName, String[] whereNames,
			String likeValue) {
		return queryByLike(tableName, null, whereNames, likeValue);
	}

	public Cursor queryByLike(String tableName, String[] selectNames,
			String[] whereNames, String likeValue) {
		StringBuilder builder = new StringBuilder();
		builder.append("select ");
		if (selectNames == null)
			builder.append("* ");
		else {
			for (String select : selectNames)
				builder.append(select).append(",");
			builder.replace(builder.length() - 1, builder.length(), " ");
		}
		builder.append("from ").append(tableName).append(" where ");
		for (String where : whereNames)
			builder.append(where).append(" like \'%").append(likeValue)
					.append("%\' or ");
		builder.replace(builder.length() - 3, builder.length(), "");
		String sql = builder.toString();
		return mSQLiteDb.rawQuery(sql, null);
	}

	// **********************************************************
	// 私有方法

	private void close() {
		mSQLiteDb.close();
		mDbHelperUtil.close();
	}

}
