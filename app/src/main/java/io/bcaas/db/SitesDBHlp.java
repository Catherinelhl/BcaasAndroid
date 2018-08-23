package io.bcaas.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SitesDBHlp extends SQLiteOpenHelper {

	private static final String TAG = SitesDBHlp.class.getName();

	private static final String DATABASE_NAME = "Bcaas";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "BcaasSecretKey";
	private static final String TABLE_CREATE =
								" CREATE TABLE " + TABLE_NAME + " ( " +
								" uid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT " +
								" keyStore TEXT NOT NULL " +
								" createTime DATETIME DEFAULT CURRENT_TIMESTAMP ) ";

	private static final String COLUMN_UID = "uid";
	private static final String COLUMN_KEYSTORE = "keyStore";
	private static final String COLUMN_CREATETIME = "createTime";



	public SitesDBHlp(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(TABLE_CREATE);
//		Log.e("SitesDBHlp onCreate","執行創立一個TABLE");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	//刪除UID
//	public int deleteDB(Integer uid){
//		SQLiteDatabase db = getWritableDatabase();
//		String whereClause = COL_UID + "='" + uid + "'";
//		int count = db.delete(TABLE_NAME, whereClause, null);
//		db.close();
//		return count;
//	}
//	//查詢TABLE是否存在
//	public boolean selectTable(){
//		boolean sel = false;
//		SQLiteDatabase db = getWritableDatabase();
//		Cursor cursor = null;
//		try
//		{
//			String sql = "SELECT count(*) as c FROM sqlite_master WHERE type='table' and name ='"+TABLE_NAME+"'";
//			cursor = db.rawQuery(sql, null);
//            if(cursor.moveToNext()){
//                    int count = cursor.getInt(0);
//                    if(count > 0){
//                    	sel = true;
//                    	Log.e(TAG,"有這個TABLE");
//                    }
//            }
//		}
//		catch(Exception e)
//		{
//			sel = false;
//			cursor.close();
//			db.close();
//			return sel;
//		}
//		cursor.close();
//		db.close();
//		return sel;
//	}
//	//修改TABLE
//	public void updateTable(String NowTime){
//		SQLiteDatabase db = getWritableDatabase();
//		String sql = "UPDATE "+TABLE_NAME+" set "+COL_Time+"='"+NowTime+"'";
//		db.execSQL(sql);
//		db.close();
//	}
//	//刪除TABLE
//	public void deleteTable(){
//		SQLiteDatabase db = getWritableDatabase();
//		String sql = "DROP TABLE ";
//		db.execSQL(sql+TABLE_NAME);
//		db.close();
////		Log.e(TAG,"刪除TABLE");
//	}
//	//新增TABLE
//	public void insertTable(){
//		SQLiteDatabase db = getWritableDatabase();
//		db.execSQL(TABLE_CREATE);
//		db.close();
////		Log.e(TAG,"新增TABLE");
//	}
//	//新增
//	public long insertDB(String createTime){
//		SQLiteDatabase db = getWritableDatabase();
//		ContentValues values = new ContentValues();
//		values.put(COL_Time, createTime);
//		long rowId = db.insert(TABLE_NAME, null, values);
//		db.close();
//		return rowId;
//	}
	//查詢
//	public ArrayList<Site> getAllSites(){
//		SQLiteDatabase db = getWritableDatabase();
//		String[] columns = {COL_UID,COL_Time};
//		Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
//		ArrayList<Site> sites = new ArrayList<Site>();
//		while(cursor.moveToNext()){
//			Integer uid = cursor.getInt(0);
//			String createTime = cursor.getString(1);
//			Site site = new Site(uid,createTime);
//			sites.add(site);
//		}
//		cursor.close();
//		db.close();
//		return sites;
//	}
	
}
