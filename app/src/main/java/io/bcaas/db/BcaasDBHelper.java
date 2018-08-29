package io.bcaas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import io.bcaas.tools.BcaasLog;

public class BcaasDBHelper extends SQLiteOpenHelper {

    private static final String TAG = BcaasDBHelper.class.getName();

    private static final String DATABASE_NAME = "Bcaas";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "BcaasSecretKey";
    private static final String TABLE_CREATE =
            " CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                    " uid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    " keyStore TEXT NOT NULL ," +
                    " createTime DATETIME DEFAULT CURRENT_TIMESTAMP ) ";

    private static final String COLUMN_UID = "uid";
    private static final String COLUMN_KEYSTORE = "keyStore";
    private static final String COLUMN_CREATETIME = "createTime";


    public BcaasDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    /**
     * 插入Keystore信息
     *
     * @param keyStore
     * @return
     */
    public long insertKeystore(String keyStore) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEYSTORE, keyStore);
        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();
        return rowId;
    }

    /**
     * 查询当前表中是否有数据
     *
     * @return
     */
    public Boolean queryIsExistKeyStore() {
        boolean exist = false;
        SQLiteDatabase sqliteDatabase = getWritableDatabase();
        String sql = "select count(*) from " + TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = sqliteDatabase.rawQuery(sql, null);
            if (cursor.moveToNext())// 判断Cursor中是否有数据
            {
                if (cursor.getCount() > 0) {
                    exist = true;
                }
                BcaasLog.d(TAG, cursor.getInt(0) != 0);// 返回总记录数
            }
        } catch (Exception e) {
            exist = false;
            cursor.close();
            sqliteDatabase.close();
            return exist;
        }
        cursor.close();
        sqliteDatabase.close();
        return exist;// 如果没有数据，则返回0

    }


    /**
     * 更新 keystore信息：替换钱包
     *
     * @param keystore
     */
    public void updateKeystore(String keystore) {
        //1：查询当前表中是否有其他数据，有的话，就进行删除
        String keystoreOld = queryKeystoreFromDB();
        BcaasLog.d(TAG, "即将删除旧数据：" + keystoreOld);
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        //+ " where " + COLUMN_KEYSTORE + " = " + keystoreOld
        //既然当前数据库只有一条数据，那么可以就全部替换。
        String sql = "update " + TABLE_NAME + " set " + COLUMN_KEYSTORE + " ='" + keystore + "' where 1=1";
        BcaasLog.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }

    /**
     * 查询当前已存在的keystore信息
     *
     * @return
     */
    public String queryKeystoreFromDB() {
        String keystore = null;
        SQLiteDatabase sqliteDatabase = getWritableDatabase();
        String sql = "select * from " + TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = sqliteDatabase.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst())// 判断Cursor中是否有数据
                {
                    keystore = cursor.getString(cursor.getColumnIndex(COLUMN_KEYSTORE));
                    BcaasLog.d(TAG, keystore);
                }
            }
        } catch (Exception e) {
            cursor.close();
            sqliteDatabase.close();
            return keystore;
        }
        cursor.close();
        sqliteDatabase.close();
        return keystore;// 如果没有数据，则返回null
    }

    /**
     * 删除此张表，用于开发者测试用
     */
    public void deleteDB() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "delete from " + TABLE_NAME;
        BcaasLog.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }


}
