package io.bcaas.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import io.bcaas.constants.DBConstans;
import io.bcaas.tools.LogTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/29
 * <p>
 * 用于对BcaasKeystore数据表的操作
 */
public class BcaasKeystoreDAO {
    private String TAG = BcaasKeystoreDAO.class.getSimpleName();

    //BCAAS_KEYSTORE table
    private String TABLE_NAME = DBConstans.BCAAS_SECRET_KEY;//当前存储的钱包信息
    private String COLUMN_UID = DBConstans.UID;
    private String COLUMN_KEYSTORE = DBConstans.KEYSTORE;
    private String COLUMN_CREATETIME = DBConstans.CREATETIME;
    //创建存储钱包表的语句
    private String TABLE_BCAAS_KEYSTORE_CREATE =
            " CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                    " uid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    " keyStore TEXT NOT NULL ," +
                    " createTime DATETIME DEFAULT CURRENT_TIMESTAMP ) ";


    public BcaasKeystoreDAO(SQLiteDatabase database) {
        if (database != null) {
            database.execSQL(TABLE_BCAAS_KEYSTORE_CREATE);

        }
    }

    /**
     * 更新表格
     *
     * @return
     */
    public String onUpgrade() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    //----------------操作Keystore数据表------start------------------------------

    /**
     * 插入Keystore信息
     *
     * @param keyStore
     * @return
     */
    public long insertKeyStore(SQLiteDatabase sqliteDatabase, String keyStore) {
        //插入数据之前，可以先执行delete操作
        clearKeystore(sqliteDatabase);
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEYSTORE, keyStore);
        long rowId = sqliteDatabase.insert(TABLE_NAME, null, values);
        sqliteDatabase.close();
        return rowId;
    }

    /**
     * 查询当前表中是否有数据
     *
     * @return
     */
    public Boolean queryIsExistKeyStore(SQLiteDatabase sqliteDatabase) {
        boolean exist = false;
        String sql = "select count(*) from " + TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = sqliteDatabase.rawQuery(sql, null);
            if (cursor.moveToNext())// 判断Cursor中是否有数据
            {
                exist = cursor.getInt(0) != 0;
                LogTool.d(TAG, exist);// 返回总记录数
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
    public void updateKeyStore(SQLiteDatabase sqliteDatabase, String keystore) {
        //1：查询当前表中是否有其他数据，有的话，就进行删除
        String keystoreOld = queryKeyStore(sqliteDatabase);
        LogTool.d(TAG, "即将删除旧数据：" + keystoreOld);
        //+ " where " + COLUMN_KEYSTORE + " = " + keystoreOld
        //既然当前数据库只有一条数据，那么可以就全部替换。
        String sql = "update " + TABLE_NAME + " set " + COLUMN_KEYSTORE + " ='" + keystore + "' where 1=1";
        LogTool.d(TAG, sql);
        sqliteDatabase.execSQL(sql);
        sqliteDatabase.close();
    }

    /**
     * 查询当前已存在的keystore信息
     *
     * @return
     */
    public String queryKeyStore(SQLiteDatabase sqliteDatabase) {
        String keystore = null;
        String sql = "select * from " + TABLE_NAME + " ORDER BY " + COLUMN_KEYSTORE + " DESC LIMIT 1";
        Cursor cursor = null;
        try {
            cursor = sqliteDatabase.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst())// 判断Cursor中是否有数据
                {
                    keystore = cursor.getString(cursor.getColumnIndex(COLUMN_KEYSTORE));
                    LogTool.d(TAG, keystore);
                }
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();

            }
            if (sqliteDatabase != null) {
                sqliteDatabase.close();

            }
            return keystore;
        }
        if (cursor != null) {
            cursor.close();
        }
        if (sqliteDatabase != null) {
            sqliteDatabase.close();

        }
        return keystore;// 如果没有数据，则返回null
    }

    /**
     * 清空Keystore这张表的数据，用于开发者测试用
     */
    public void clearKeystore(SQLiteDatabase sqliteDatabase) {
        String sql = "delete from " + TABLE_NAME;
        LogTool.d(TAG, sql);
        sqliteDatabase.execSQL(sql);
        sqliteDatabase.close();
    }
    //----------------操作Keystore数据表------end------------------------------

}
