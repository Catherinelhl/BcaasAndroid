package io.bcaas.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.List;

import io.bcaas.base.BaseFragment;

/**
 * @projectName: BottomBar
 * @packageName: cn.catherine.bottombar.adapter
 * @author: catherine
 * @time: 2018/9/10
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragmentList;

    public FragmentAdapter(FragmentManager fm, List<BaseFragment> mFragmentList) {
        super(fm);
        this.mFragmentList = mFragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        return mFragmentList == null ? null : mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        //        super.destroyItem(container, position, object);
    }
}
