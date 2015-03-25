package bb.stone.falling.databases;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import bb.stone.falling.entry.SensorData;

//参考：http://blog.csdn.net/liuhe688/article/details/6715983
public class DBManager
{
	private DatabaseHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context)
	{
		helper = new DatabaseHelper(context);
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里

	}

	/**
	 * add persons
	 * 
	 * @param persons
	 */
	public void add(final List<SensorData> sds)
	{
		db = helper.getWritableDatabase();
		if (db.isOpen()) {
			// 采用事务处理，确保数据完整性
			db.beginTransaction(); // 开始事务
			try
			{
				for (SensorData sd : sds)
				{
					db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME
							+ " VALUES(?,?,?,?,?, ?, ?)", new Object[] { sd.getTime(),sd.getxA(),sd.getyA(),sd.getzA(),
							sd.getxD(),sd.getyD(),sd.getzD()});
					// 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
					// 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
					// 使用占位符有效区分了这种情况
				}
				db.setTransactionSuccessful(); // 设置事务成功完成
			}
			finally
			{
				db.endTransaction(); // 结束事务
				db.close();
			}
		}
	}

	/**
	 * update person's age
	 * 
	 * @param person
	 */
	public void updateAge(SensorData sd)
	{
		db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("time", sd.getTime());
		db.update(DatabaseHelper.TABLE_NAME, cv, "time = ?",
				new String[] { String.valueOf(sd.getTime()) });
	}

	/**
	 * delete old person
	 * 
	 * @param person
	 */
	public void deleteOldSensorData(SensorData sd)
	{
		db = helper.getWritableDatabase();
		db.delete(DatabaseHelper.TABLE_NAME, "time >= ?",
				new String[] { String.valueOf(sd.getTime()) });
	}

	/**
	 * query all persons, return list
	 * 
	 * @return List<Person>
	 */
	public List<SensorData> query()
	{
		db = helper.getReadableDatabase();
		ArrayList<SensorData> sds = new ArrayList<SensorData>();
		Cursor c = queryTheCursor();
		while (c.moveToNext())
		{
			SensorData sd = new SensorData();
			sd.setTime(c.getLong(c.getColumnIndex("time")));
			sd.setxA(c.getFloat(c.getColumnIndex("xA")));
			sd.setxA(c.getFloat(c.getColumnIndex("yA")));
			sd.setxA(c.getFloat(c.getColumnIndex("zA")));
			sd.setxA(c.getFloat(c.getColumnIndex("xD")));
			sd.setxA(c.getFloat(c.getColumnIndex("yD")));
			sd.setxA(c.getFloat(c.getColumnIndex("zD")));
			sd.setxA(c.getFloat(c.getColumnIndex("cD")));
			sds.add(sd);
		}
		c.close();
		return sds;
	}

	/**
	 * query all persons, return cursor
	 * 
	 * @return Cursor
	 */
	public Cursor queryTheCursor()
	{
		Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME,
				null);
		return c;
	}

	/**
	 * close database
	 */
	public void closeDB()
	{
		// 释放数据库资源
		db.close();
	}

	public int size() {
		db=helper.getReadableDatabase();
		String sql="SELECT * FROM " +DatabaseHelper.TABLE_NAME;
		Cursor cursor = db.rawQuery( sql, null);
		int size=cursor.getCount();
		cursor.close();
		return size;
	}

}