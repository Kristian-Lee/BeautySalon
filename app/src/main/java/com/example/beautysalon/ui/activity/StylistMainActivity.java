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
    private String titleText[] = {"้ฆ้กต", "ๆ็"};


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

        //ๅฎไพๅviewpager็้้ๅจๅนถ่ฎพ็ฝฎ
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mFragmentList);
        mBinding.viewPager.setAdapter(viewPagerAdapter);

        mSearchView = mBinding.searchView;
        mToolBar = mBinding.toolbar;
        setSupportActionBar(mToolBar);
        //็ถๆๆ?ๆฒๆตธ
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

        //่ฎพ็ฝฎๅบ้จๅฏผ่ช็ๅฌๅจ๏ผๅบ้จๅฏผ่ชๅๅๆถ๏ผviewpagerไน่ท็ๅๅ
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
                    //false่กจ็คบๅณ้ญๅๆขๅจๆ
                    Log.d("navigation", position + "");
                    getSupportActionBar().setTitle(titleText[position]);
                    mBinding.viewPager.setCurrentItem(position, false);
                }
                return true;
            }
        });
    }


    //viewpager้้ๅจ๏ผๆ?นๆฎไฝ็ฝฎ่ฟๅๅฏนๅบ็fragment
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

    //ๅฏผๅฅๅนถ่ฎพ็ฝฎ่ๅ
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
        //ๆพๅฐๅนถ่ฎพ็ฝฎๅผๅฏsearchview็item
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);

        mSearchView.setBackIconColor(Color.parseColor("#5872fd"));
        // Adding padding to the animation because of the hidden menu item
        Point revealCenter = mSearchView.getRevealAnimationCenter();
        revealCenter.x -= DimensUtils.convertDpToPx(EXTRA_REVEAL_CENTER_PADDING, this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSearchView.isSearchOpen()) {
                mSearchView.closeSearch();
                return true;
            }
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(StylistMainActivity.this, "ๅๆไธๆฌก้ๅบ็จๅบ",Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}