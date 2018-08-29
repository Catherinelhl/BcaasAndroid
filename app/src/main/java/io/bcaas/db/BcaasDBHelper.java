package io.bcaas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.constants.Constants;
import io.bcaas.db.vo.Address;
import io.bcaas.tools.BcaasLog;

/**
 * 数据库：管理当前钱包Keystore，以及钱包地址
 */

public class BcaasDBHelper extends SQLiteOpenHelper {

    private static final String TAG = BcaasDBHelper.class.getName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = Constants.DB.DB_NAME;


    //BCAAS_KEYSTORE table
    private static class BCAAS_KEYSTORE {
        private static final String TABLE_NAME = Constants.DB.BCAAS_SECRET_KEY;//当前存储的钱包信息
        private static final String COLUMN_UID = Constants.DB.UID;
        private static final String COLUMN_KEYSTORE = Constants.DB.KEYSTORE;
        private static final String COLUMN_CREATETIME = Constants.DB.CREATETIME;
        //创建存储钱包表的语句
        private static final String TABLE_BCAAS_KEYSTORE_CREATE =
                " CREATE TABLE IF NOT EXISTS " + BCAAS_KEYSTORE.TABLE_NAME + " ( " +
                        " uid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        " keyStore TEXT NOT NULL ," +
                        " createTime DATETIME DEFAULT CURRENT_TIMESTAMP ) ";


    }

    //BCAAS_Adress table
    private static class BCAAS_ADDRESS {
        private static final String TABLE_NAME = Constants.DB.BCAAS_ADDRESS;//当前存储的地址信息
        private static final String COLUMN_UID = Constants.DB.UID;
        private static final String COLUMN_ADDRESS_NAME = Constants.DB.ADDRESS_NAME;
        private static final String COLUMN_ADDRESS = Constants.DB.ADDRESS;
        private static final String COLUMN_CREATETIME = Constants.DB.CREATETIME;
        //创建存储地址表的语句
        private static final String TABLE_BCAAS_ADDRESS =
                " CREATE TABLE IF NOT EXISTS " + BCAAS_ADDRESS.TABLE_NAME + " ( " +
                        " uid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        " addressName TEXT NOT NULL," +
                        " address TEXT NOT NULL, " +
                        " createTime DATETIME DEFAULT CURRENT_TIMESTAMP ) ";

    }

    public BcaasDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BCAAS_KEYSTORE.TABLE_BCAAS_KEYSTORE_CREATE);
        db.execSQL(BCAAS_ADDRESS.TABLE_BCAAS_ADDRESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlBcassKeystore = "DROP TABLE IF EXISTS " + BCAAS_KEYSTORE.TABLE_NAME;
        String sqlBcaasAddress = "DROP TABLE IF EXISTS " + BCAAS_ADDRESS.TABLE_NAME;
        db.execSQL(sqlBcassKeystore);
        db.execSQL(sqlBcaasAddress);
        onCreate(db);
    }
    //----------------操作Keystore数据表------start------------------------------

    /**
     * 插入Keystore信息
     *
     * @param keyStore
     * @return
     */
    public long insertKeystore(String keyStore) {
        //插入数据之前，可以先执行delete操作
        clearKeystoreTable();
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BCAAS_KEYSTORE.COLUMN_KEYSTORE, keyStore);
        long rowId = db.insert(BCAAS_KEYSTORE.TABLE_NAME, null, values);
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
        String sql = "select count(*) from " + BCAAS_KEYSTORE.TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = sqliteDatabase.rawQuery(sql, null);
            if (cursor.moveToNext())// 判断Cursor中是否有数据
            {
                exist = cursor.getInt(0) != 0;
                BcaasLog.d(TAG, exist);// 返回总记录数
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
        String sql = "update " + BCAAS_KEYSTORE.TABLE_NAME + " set " + BCAAS_KEYSTORE.COLUMN_KEYSTORE + " ='" + keystore + "' where 1=1";
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
        String sql = "select * from " + BCAAS_KEYSTORE.TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = sqliteDatabase.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst())// 判断Cursor中是否有数据
                {
                    keystore = cursor.getString(cursor.getColumnIndex(BCAAS_KEYSTORE.COLUMN_KEYSTORE));
                    BcaasLog.d(TAG, keystore);
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
    public void clearKeystoreTable() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "delete from " + BCAAS_KEYSTORE.TABLE_NAME;
        BcaasLog.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }
    //----------------操作Keystore数据表------end------------------------------


    //----------------操作Address数据表------start------------------------------

    /**
     * 插入Address
     *
     * @param address
     * @return
     */
    public long insertAddress(Address address) {
        if (address != null) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BCAAS_ADDRESS.COLUMN_ADDRESS, address.getAddress());
            values.put(BCAAS_ADDRESS.COLUMN_ADDRESS_NAME, address.getAddressName());
            long rowId = db.insert(BCAAS_ADDRESS.TABLE_NAME, null, values);
            db.close();
            return rowId;
        }
        return 0;
    }

    /**
     * 更新 Address信息
     *
     * @param address
     */
    public void updateAddress(Address address) {
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//        //+ " where " + COLUMN_KEYSTORE + " = " + keystoreOld
//        //既然当前数据库只有一条数据，那么可以就全部替换。
//        String sql = "update " + BCAAS_ADDRESS.TABLE_NAME + " set " + BCAAS_KEYSTORE.COLUMN_KEYSTORE + " ='" + keystore + "' where 1=1";
//        BcaasLog.d(TAG, sql);
//        sqLiteDatabase.execSQL(sql);
//        sqLiteDatabase.close();
    }

    /**
     * 查询当前所有的地址信息
     *
     * @return
     */
    public List<Address> queryAddressesFromDB() {
        List<Address> addresses = new ArrayList<>();
        SQLiteDatabase sqliteDatabase = getWritableDatabase();
        String sql = "select * from " + BCAAS_ADDRESS.TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = sqliteDatabase.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext())// 判断Cursor中是否有数据
                {
                    String addressName = cursor.getString(cursor.getColumnIndex(BCAAS_ADDRESS.COLUMN_ADDRESS_NAME));
                    String address = cursor.getString(cursor.getColumnIndex(BCAAS_ADDRESS.COLUMN_ADDRESS));
                    Address addressVO = new Address(address, addressName);
                    addresses.add(addressVO);
                    BcaasLog.d(TAG, addressVO);
                }
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            sqliteDatabase.close();
            return addresses;
        }
        if (cursor != null) {
            cursor.close();
        }
        if (sqliteDatabase != null) {
            sqliteDatabase.close();
        }
        return addresses;// 如果没有数据，则返回null
    }

    /**
     * 清空Address这张表的数据，用于开发者测试用
     */
    public void clearAddressTable() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "delete from " + BCAAS_ADDRESS.TABLE_NAME;
        BcaasLog.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }

    /**
     * 根据传入的钱包地址从数据库里面删除相对应的数据
     *
     * @param address
     */
    public void deleteAddressFromDBBYAddress(String address) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "delete from " + BCAAS_ADDRESS.TABLE_NAME + " where " + BCAAS_ADDRESS.COLUMN_ADDRESS + " ='" + address + "'";
        BcaasLog.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }
    //----------------操作Address数据表------end--------------------------------

}
