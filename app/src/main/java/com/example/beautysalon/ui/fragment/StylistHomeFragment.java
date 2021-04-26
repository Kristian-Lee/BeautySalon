package com.example.beautysalon.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.beautysalon.databinding.FragmentStylistHomeBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lee
 * @date 2021.4.13  23:33
 * @description
 */
public class StylistHomeFragment extends Fragment {

    private FragmentStylistHomeBinding mBinding;
    private static StylistHomeFragment mInstance;
    private List<Fragment> mFragmentList;
    private int previousPosition = -1;

    public static synchronized StylistHomeFragment getInstance() {
        if (mInstance == null) {
            mInstance = new StylistHomeFragment();
        }
        return mInstance;
    }

    public StylistHomeFragment() {
    }

    public static StylistHomeFragment newInstance(Bundle bundle) {
        StylistHomeFragment fragment = getInstance();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentStylistHomeBinding.inflate(inflater);
        initData();
        setListeners();
        return mBinding.getRoot();
    }

    public void initData() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(StylistReservationFragment.newInstance("today"));
        mFragmentList.add(StylistReservationFragment.newInstance("tomorrow"));
        //实例化viewpager的适配器并设置
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), mFragmentList);
        mBinding.viewPager.setAdapter(viewPagerAdapter);
        mBinding.tablayout.setupWithViewPager(mBinding.viewPager);
        mBinding.tablayout.getTabAt(0).setText("今天预约");
        mBinding.tablayout.getTabAt(1).setText("明天预约");
    }

    private void setListeners() {
        //设置viewpager滚动监听器，viewpager滚动时，底部导航也跟着变化
        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                previousPosition = position;
                mBinding.tablayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBinding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBinding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //viewpager适配器，根据位置返回对应的fragment
    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;
        ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
