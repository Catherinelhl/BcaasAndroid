package io.bcaas.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.constants.DBConstans;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/29
 * 用于对BcaasAddress数据表的操作
 */
public class AddressDAO {
    private String TAG = AddressDAO.class.getSimpleName();

    //BCAAS_Address table
    private String TABLE_NAME = DBConstans.BCAAS_ADDRESS;//当前存储的地址信息
    private String COLUMN_UID = DBConstans.UID;
    private String COLUMN_ADDRESS_NAME = DBConstans.ADDRESS_NAME;
    private String COLUMN_ADDRESS = DBConstans.ADDRESS;
    private String COLUMN_CREATE_TIME = DBConstans.CREATE_TIME;
    //创建存储地址表的语句
    private String TABLE_BCAAS_ADDRESS =
            " CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                    " uid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    " addressName TEXT NOT NULL," +
                    " address TEXT NOT NULL, " +
                    " createTime DATETIME DEFAULT CURRENT_TIMESTAMP ) ";


    public AddressDAO(SQLiteDatabase database) {
        if (database != null) {
            database.execSQL(TABLE_BCAAS_ADDRESS);

        }
    }

    public String onUpgrade() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


    //----------------操作Address数据表------start------------------------------

    /**
     * 插入Address
     *
     * @param addressVO
     * @return
     */
    public long insertAddress(SQLiteDatabase sqliteDatabase, AddressVO addressVO) {
        if (addressVO != null) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ADDRESS, addressVO.getAddress());
            values.put(COLUMN_ADDRESS_NAME, addressVO.getAddressName());
            long rowId = sqliteDatabase.insert(TABLE_NAME, null, values);
            sqliteDatabase.close();
            return rowId;
        }
        return 0;
    }

    /**
     * 查询当前所有的地址信息
     *
     * @return
     */
    public List<AddressVO> queryAddress(SQLiteDatabase sqliteDatabase) {
        List<AddressVO> addressVOS = new ArrayList<>();
        /*SELECT * FROM BcaasAddress ORDER BY uid DESC;
         SELECT * FROM BcaasAddress ORDER BY uid DESC LIMIT 0, 50;*/
        String sql = "select * from " + TABLE_NAME + " ORDER BY " + COLUMN_UID + " DESC";
        Cursor cursor = null;
        try {
            cursor = sqliteDatabase.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext())// 判断Cursor中是否有数据
                {
                    int uid = cursor.getInt(cursor.getColumnIndex(COLUMN_UID));
                    long createTime = cursor.getLong(cursor.getColumnIndex(COLUMN_CREATE_TIME));
                    String addressName = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS_NAME));
                    String address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                    AddressVO addressVO = new AddressVO(uid, createTime, address, addressName);
                    addressVOS.add(addressVO);
                }
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            sqliteDatabase.close();
            return addressVOS;
        }
        if (cursor != null) {
            cursor.close();
        }
        if (sqliteDatabase != null) {
            sqliteDatabase.close();
        }
        return addressVOS;// 如果没有数据，则返回null
    }

    /**
     * 清空Address这张表的数据，用于开发者测试用
     */
    public void clearAddress(SQLiteDatabase sqliteDatabase) {
        String sql = "delete from " + TABLE_NAME;
        LogTool.d(TAG, sql);
        sqliteDatabase.execSQL(sql);
        sqliteDatabase.close();
    }

    /**
     * 根据传入的钱包地址从数据库里面删除相对应的数据
     *
     * @param address
     */
    public void deleteAddress(SQLiteDatabase sqliteDatabase, String address) {
        String sql = "delete from " + TABLE_NAME + " where " + COLUMN_ADDRESS + " ='" + address + "'";
        LogTool.d(TAG, sql);
        sqliteDatabase.execSQL(sql);
        sqliteDatabase.close();
    }

    /**
     * 判斷當前是否有重複的地址活著命名信息
     *
     * @param sqLiteDatabase
     * @param addressVo
     * @return 返回int，表示當前的狀態
     * <p>
     * 0：代表空數據
     * 1：代表地址命名重複
     * 2：代表地址重複
     * -1：代表不重複
     */
    public int queryIsExistAddress(SQLiteDatabase sqLiteDatabase, AddressVO addressVo) {
        String address, addressName;
        int status = 0;
        if (addressVo == null) {
            return status;//返回存在，不进行存储
        }
        //取得当前存储的地址信息
        address = addressVo.getAddress();
        //取得当前存储的地址名字
        addressName = addressVo.getAddressName();
        if (StringTool.isEmpty(address)
                || StringTool.isEmpty(addressName)) {
            return status;
        }
        //查询当前是否有命名相同的数据
        if (checkRepeatData(sqLiteDatabase, COLUMN_ADDRESS_NAME, addressName)) {
            status = 1;
        } else {
            //查询当前是否有地址相同
            boolean exist = checkRepeatData(sqLiteDatabase, COLUMN_ADDRESS, address);
            if (exist) {
                status = 2;
            } else {
                status = -1;
            }
        }
        sqLiteDatabase.close();
        return status;
    }

    /**
     * 檢查重複數據
     *
     * @param sqLiteDatabase
     * @param columnName
     * @param dataName
     */
    private boolean checkRepeatData(SQLiteDatabase sqLiteDatabase, String columnName, String dataName) {
        //查询当前是否有命名相同的数据
        String sql = "select count(*) from " + TABLE_NAME + " where " + columnName + " ='" + dataName + "'";
        boolean exist = false;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, null);
            if (cursor.moveToNext())// 判断Cursor中是否有数据
            {
                exist = cursor.getInt(0) != 0;
            }
        } catch (Exception e) {
            exist = false;
        }
        cursor.close();
        return exist;
    }
    //----------------操作Address数据表------end--------------------------------

}
