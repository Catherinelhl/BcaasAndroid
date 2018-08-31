package io.bcaas.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import io.bcaas.constants.Constants;
import io.bcaas.db.BcaasDBHelper;
import io.bcaas.tools.BcaasLog;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/29
 * <p>
 * 用于对BcaasKeystore数据表的操作
 */
public class BcassKeystoreDAO {

    private String TAG = BcassKeystoreDAO.class.getSimpleName();

    private BcaasDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static final String TABLE_NAME = Constants.DB.BCAAS_SECRET_KEY;//当前存储的钱包信息
    private static final String COLUMN_UID = Constants.DB.UID;
    private static final String COLUMN_KEYSTORE = Constants.DB.KEYSTORE;
    private static final String COLUMN_CREATETIME = Constants.DB.CREATETIME;
    //创建存储钱包表的语句
    private static final String TABLE_BCAAS_KEYSTORE_CREATE =
            " CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                    " uid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    " keyStore TEXT NOT NULL ," +
                    " createTime DATETIME DEFAULT CURRENT_TIMESTAMP ) ";


    public BcassKeystoreDAO(Context context) {
        dbHelper = new BcaasDBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    /**
     * 插入Keystore信息
     *
     * @param keyStore
     * @return
     */
    public long insertKeystore(String keyStore) {
        //插入数据之前，可以先执行delete操作
        clearTable();
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEYSTORE, keyStore);
        long rowId = sqLiteDatabase.insert(TABLE_NAME, null, values);
        sqLiteDatabase.close();
        return rowId;
    }

    /**
     * 查询当前表中是否有数据
     *
     * @return
     */
    public Boolean queryIsExistKeyStore() {
        boolean exist = false;
        String sql = "select count(*) from " + TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, null);
            if (cursor.moveToNext())// 判断Cursor中是否有数据
            {
                exist = cursor.getInt(0) != 0;
                BcaasLog.d(TAG, exist);// 返回总记录数
            }
        } catch (Exception e) {
            exist = false;
            cursor.close();
            sqLiteDatabase.close();
            return exist;
        }
        cursor.close();
        sqLiteDatabase.close();
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
        String sql = "select * from " + TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst())// 判断Cursor中是否有数据
                {
                    keystore = cursor.getString(cursor.getColumnIndex(COLUMN_KEYSTORE));
                    BcaasLog.d(TAG, keystore);
                }
            }
        } catch (Exception e) {
            cursor.close();
            sqLiteDatabase.close();
            return keystore;
        }
        cursor.close();
        sqLiteDatabase.close();
        return keystore;// 如果没有数据，则返回null
    }

    /**
     * 清空这张表的数据，用于开发者测试用
     */
    public void clearTable() {
        String sql = "delete from " + TABLE_NAME;
        BcaasLog.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }
}
