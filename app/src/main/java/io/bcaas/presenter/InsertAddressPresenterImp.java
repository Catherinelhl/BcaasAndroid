package io.bcaas.presenter;


import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.ui.contracts.InsertAddressContract;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 * Presenter：「添加地址」界面需要的數據獲取&處理
 */
public class InsertAddressPresenterImp implements InsertAddressContract.Presenter {

    private InsertAddressContract.View view;

    public InsertAddressPresenterImp(InsertAddressContract.View view) {
        super();
        this.view = view;

    }

    /*將當前新添加的一條數據添加到本地數據庫*/
    @Override
    public void saveData(AddressVO addressVO) {
        //向数据库里面插入新添加的地址信息
        if (addressVO == null) {
            return;
        }
        if (BCAASApplication.bcaasDBHelper != null) {
            view.hideLoading();
            int status = BCAASApplication.bcaasDBHelper.queryIsExistAddress(addressVO);
            switch (status) {
                case -1://沒有重複的
                    long result = BCAASApplication.bcaasDBHelper.insertAddress(addressVO);
                    if (result == 0) {
                        view.saveDataFailure();
                    } else {
                        view.saveDataSuccess();

                    }
                    break;
                case 0://數據異常
                    view.saveDataFailure();
                    break;
                case 1://命名重複
                    view.addressRepeat(BCAASApplication.context().getResources().getString(R.string.address_name_repeat));
                    break;
                case 2://地址重複
                    view.addressRepeat(BCAASApplication.context().getResources().getString(R.string.address_repeat));
                    break;
            }

        }


    }

}
