package com.example.beautysalon.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.beautysalon.R;
import com.example.beautysalon.databinding.ActivityStylistMainBinding;
import com.example.beautysalon.ui.fragment.StylistFragment;
import com.example.beautysalon.ui.fragment.StylistHomeFragment;
import com.example.beautysalon.utils.Utils;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.ferfalk.simplesearchview.utils.DimensUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

public class StylistMainActivity extends AppCompatActivity {

    public static final int EXTRA_REVEAL_CENTER_PADDING = 40;
    private ActivityStylistMainBinding mBinding;
    private SimpleSearchView mSearchView;
    private Toolbar mToolBar;
    private long mExitTime;
    private List<Fragment> mFragmentList;
    private SparseIntArray items;
    private int previousPosition = -1;
    private String titleText[] = {"首页", "我的"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityStylistMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
    }

    private void initData() {
        mBinding.bottomNavigationView.enableAnimation(true);
        mBinding.bottomNavigationView.enableShiftingMode(true);
        mBinding.bottomNavigationView.enableItemShiftingMode(true);
        mBinding.bottomNavigationView.setIconSize(20);
        mBinding.bottomNavigationView.setTextSize(10);

        items = new SparseIntArray(2);
        items.put(R.id.navigation_home, 0);
        items.put(R.id.navigation_my, 1);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(StylistHomeFragment.newInstance(getIntent().getExtras()));
        mFragmentList.add(StylistFragment.newInstance(getIntent().getExtras()));

        //实例化viewpager的适配器并设置
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mFragmentList);
        mBinding.viewPager.setAdapter(viewPagerAdapter);

        mSearchView = mBinding.searchView;
        mToolBar = mBinding.toolbar;
        setSupportActionBar(mToolBar);
        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }

    private void setListeners() {
        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                previousPosition = position;
                Log.d("pageselected", position + "");
                mBinding.bottomNavigationView.setCurrentItem(position);
                getSupportActionBar().setTitle(titleText[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //设置底部导航监听器，底部导航变化时，viewpager也跟着变化
        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

//                switch (item.getItemId()) {
//                    case R.id.navigation_home:
//                        viewPager.setCurrentItem(0);
//                    case R.id.navigation_message:
//                        viewPager.setCurrentItem(1);
//                    case R.id.navigation_my:
//                        viewPager.setCurrentItem(2);
//                }
                int position = items.get(item.getItemId());
                if (previousPosition != position) {
                    // only set item when item changed
                    previousPosition = position;
                    //false表示关闭切换动效
                    Log.d("navigation", position + "");
                    getSupportActionBar().setTitle(titleText[position]);
                    mBinding.viewPager.setCurrentItem(position, false);
                }
                return true;
            }
        });
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

    //导入并设置菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        setupSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_exit) {
            Utils.saveToken(StylistMainActivity.this, "");
            Intent intent = new Intent(StylistMainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupSearchView(Menu menu) {
        //找到并设置开启searchview的item
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);

        mSearchView.setBackIconColor(Color.parseColor("#5872fd"));
        // Adding padding to the animation because of the hidden menu item
        Point revealCenter = mSearchView.getRevealAnimationCenter();
        revealCenter.x -= DimensUtils.convertDpToPx(EXTRA_REVEAL_CENTER_PADDING, this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSearchView.isSearchOpen()) {
                mSearchView.closeSearch();
                return true;
            }
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(StylistMainActivity.this, "再按一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}