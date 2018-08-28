package io.bcaas.presenter;


import io.bcaas.R;
import io.bcaas.base.BasePresenterImp;
import io.bcaas.database.Address;
import io.bcaas.ui.contracts.InsertAddressContract;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 */
public class InsertAddressPresenterImp
        extends BasePresenterImp
        implements InsertAddressContract.Presenter {

    private InsertAddressContract.View view;

    public InsertAddressPresenterImp(InsertAddressContract.View view) {
        super();
        this.view = view;

    }

    /*將當前新添加的一條數據添加到本地數據庫*/
    @Override
    public void saveData(Address address) {
        view.showLoadingDialog(getString(R.string.loading));
        insertAddressDataTODB(address);
        view.saveDataSuccess();
        view.hideLoadingDialog();

    }
}
