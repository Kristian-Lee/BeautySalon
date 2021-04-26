package com.example.beautysalon.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.beautysalon.databinding.ActivityMyBalanceBinding;
import com.example.beautysalon.ui.fragment.MyBalanceFragment;
import com.google.android.material.tabs.TabLayout;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;



public class MyBalanceActivity extends AppCompatActivity {

    private ActivityMyBalanceBinding mBinding;
    private List<Fragment> mFragments;
    private int previousPosition = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMyBalanceBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
    }

    public void initData() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFragments = new ArrayList<>();
        mFragments.add(MyBalanceFragment.newInstance("all"));
        mFragments.add(MyBalanceFragment.newInstance("income"));
        mFragments.add(MyBalanceFragment.newInstance("expense"));
        //实例化viewpager的适配器并设置
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mFragments);
        mBinding.viewPager.setAdapter(viewPagerAdapter);
        mBinding.tablayout.setupWithViewPager(mBinding.viewPager);
        mBinding.tablayout.getTabAt(0).setText("全部");
        mBinding.tablayout.getTabAt(1).setText("收入");
        mBinding.tablayout.getTabAt(2).setText("支出");



        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
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
                System.out.println("选中第" + tab.getPosition() + "个tab");
                System.out.println(mFragments.get(tab.getPosition()).getArguments().getString("type"));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //viewpager适配器，根据位置返回对应的fragment
    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;
        ViewPagerAdapter(FragmentManager fm, int behavior, List<Fragment> fragments) {
            super(fm, behavior);
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