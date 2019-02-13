package io.bcaas.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.constants.DBConstans;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.dao.AddressDAO;
import io.bcaas.db.dao.KeystoreDAO;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.tools.LogTool;

/**
 * @author catherine.brainwilliam
 * 數據庫：
 * 1：存儲當前Wallet Keystore屬性信息
 * 2：存儲當前添加Wallet地址信息
 */

public class BcaasDBHelper extends SQLiteOpenHelper {

    private String TAG = BcaasDBHelper.class.getName();
    //当前数据库的版本
    public static int DATABASE_VERSION = 1;
    //创建存储用户信息的数据表操作类
    private KeystoreDAO keystoreDAO;
    //创建存储钱包地址信息的数据表操作类
    private AddressDAO addressDAO;

    public BcaasDBHelper(Context context) {
        super(context, DBConstans.DB_NAME, null, DATABASE_VERSION);
        keystoreDAO = new KeystoreDAO(getWritableDatabase());
        addressDAO = new AddressDAO(getWritableDatabase());

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogTool.d(TAG, "onCreate");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(keystoreDAO.onUpgrade());
        db.execSQL(addressDAO.onUpgrade());
        onCreate(db);
    }
    //----------------操作Keystore数据表------start------------------------------

    /**
     * 插入Keystore信息
     *
     * @param keyStore
     * @return
     */
    public long insertKeyStore(String keyStore) {
        if (addressDAO == null) {
            return 0;
        }
        //插入数据之前，可以先执行delete操作
        clearKeystore(getWritableDatabase());
        return keystoreDAO.insertKeyStore(getWritableDatabase(), keyStore);
    }

    /**
     * 清空Keystore这张表的数据，用于开发者测试用
     */
    public void clearKeystore(SQLiteDatabase sqliteDatabase) {
        String sql = "delete from " + DBConstans.BCAAS_SECRET_KEY;
        LogTool.d(TAG, sql);
        sqliteDatabase.execSQL(sql);
        sqliteDatabase.close();
    }

    /**
     * 查询当前表中是否有数据
     *
     * @return
     */
    public Boolean queryIsExistKeyStore() {
        if (keystoreDAO == null) {
            return false;
        }
        return keystoreDAO.queryIsExistKeyStore(getWritableDatabase());// 如果没有数据，则返回0
    }


    /**
     * 更新 keystore信息：替换钱包
     *
     * @param keystore
     */
    public void updateKeyStore(String keystore) {
        if (keystoreDAO == null) {
            return;
        }
        keystoreDAO.updateKeyStore(getWritableDatabase(), keystore);
    }

    /**
     * 查询当前已存在的keystore信息
     *
     * @return
     */
    public String queryKeyStore() {
        if (keystoreDAO == null) {
            return MessageConstants.Empty;
        }
        return keystoreDAO.queryKeyStore(getWritableDatabase(), true);// 如果没有数据，则返回null
    }
    //----------------操作Keystore数据表------end------------------------------


    //----------------操作Address数据表------start------------------------------

    /**
     * 插入Address
     *
     * @param addressVO
     * @return
     */
    public long insertAddress(AddressVO addressVO) {
        if (addressDAO == null) {
            return 0;
        }
        return addressDAO.insertAddress(getWritableDatabase(), addressVO);
    }

    /**
     * 查询当前所有的地址信息
     *
     * @return
     */
    public List<AddressVO> queryAddress() {
        if (addressDAO == null) {
            return new ArrayList<>();
        }
        return addressDAO.queryAddress(getWritableDatabase());// 如果没有数据，则返回null
    }

    /**
     * 清空Address这张表的数据，用于开发者测试用
     */
    public void clearAddress() {
        if (addressDAO == null) {
            return;
        }
        addressDAO.clearAddress(getWritableDatabase());
    }

    /**
     * 根据传入的钱包地址从数据库里面删除相对应的数据
     *
     * @param address
     */
    public void deleteAddress(String address) {
        if (addressDAO == null) {
            return;
        }
        addressDAO.deleteAddress(getWritableDatabase(), address);
    }

    /**
     * 查询当前是否存储这个地址
     *
     * @param addressVo
     */
    public int queryIsExistAddress(AddressVO addressVo) {
        if (addressDAO == null) {
            return -1;
        }
        return addressDAO.queryIsExistAddress(getWritableDatabase(), addressVo);
    }

    //----------------操作Address数据表------end--------------------------------

}
