package com.example.beautysalon.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.ActivityMainBinding;
import com.example.beautysalon.ui.fragment.HomeFragment;
import com.example.beautysalon.ui.fragment.MyFragment;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.ferfalk.simplesearchview.utils.DimensUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gyf.immersionbar.ImmersionBar;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends AppCompatActivity {

    public static final int EXTRA_REVEAL_CENTER_PADDING = 40;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private long mExitTime;
    private SimpleSearchView searchView;
    private List<Fragment> fragments;
    private SparseIntArray items;
    private int previousPosition = -1;
    private ActivityMainBinding binding;
    private Badge mBadge = null;
    private String titleText[] = {"首页", "我的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        searchView = binding.searchView;

        BottomNavigationViewEx bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.enableAnimation(true);
        bottomNavigationView.enableShiftingMode(true);
        bottomNavigationView.enableItemShiftingMode(true);
        bottomNavigationView.setIconSize(20);
        bottomNavigationView.setTextSize(10);
        items = new SparseIntArray(2);
        items.put(R.id.navigation_home, 0);
        items.put(R.id.navigation_my, 1);
        fragments = new ArrayList<>();
        fragments.add(HomeFragment.newInstance(getIntent().getExtras()));
        fragments.add(MyFragment.newInstance(getIntent().getExtras()));

        //实例化viewpager的适配器并设置
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        ViewPager viewPager = binding.container;
        viewPager.setAdapter(viewPagerAdapter);

//        NavController navController = Navigation.findNavController();
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //设置viewpager滚动监听器，viewpager滚动时，底部导航也跟着变化
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                previousPosition = position;
                Log.d("pageselected", position + "");
                bottomNavigationView.setCurrentItem(position);
                getSupportActionBar().setTitle(titleText[position]);
                searchView.closeSearch();
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //设置底部导航监听器，底部导航变化时，viewpager也跟着变化
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

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
                    viewPager.setCurrentItem(position, false);
                    searchView.closeSearch();
                    invalidateOptionsMenu();
                }
                return true;
            }
        });



        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(binding.view)
                .init();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); //这一句必须的，否则Intent无法获得最新的数据
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForUnpaid();
    }

    private void checkForUnpaid() {
        UserDao userDao = (UserDao) getIntent().getExtras().getSerializable("user");
        NetClient.getNetClient().callNet(NetworkSettings.RESERVATION_DATA, "type", "unpaid",
                RequestBody.create(JSON.toJSONString(userDao), Utils.MEDIA_TYPE),
                new NetClient.MyCallBack() {
                    @Override
                    public void onFailure(int code) {
                        mMessage.what = ResponseCode.REQUEST_RESERVATION_DATA_FAILED;
                        mHandler.post(()-> Utils.showMessage(MainActivity.this, mMessage));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                mMessage.what = restResponse.getCode();
                                if (mMessage.what == ResponseCode.REQUEST_RESERVATION_DATA_SUCCESS) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                                    List<ReserveDao> reserveDaoList = Utils.jsonToList(ReserveDao.class,
                                            JSON.parseObject(map.get("reserve").toString(), List.class));
                                    ((MyFragment) fragments.get(1)).setBadgeNum(reserveDaoList.size());
                                    if (reserveDaoList.size() > 0) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mBadge == null) {
                                                    addBadgeAt(1, reserveDaoList.size());
                                                } else {
                                                    mBadge.setBadgeNumber(reserveDaoList.size());
                                                }
                                            }
                                        });
                                    } else if (mBadge != null){
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mBadge.hide(true);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
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
            Utils.saveToken(MainActivity.this, "");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (binding.container.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.action_search).setVisible(true);
                break;
            case 1:
                menu.findItem(R.id.action_search).setVisible(false);
                break;
            default:
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void setupSearchView(Menu menu) {
        //找到并设置开启searchview的item
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        searchView.setBackIconColor(Color.parseColor("#5872fd"));
        // Adding padding to the animation because of the hidden menu item
        Point revealCenter = searchView.getRevealAnimationCenter();
        revealCenter.x -= DimensUtils.convertDpToPx(EXTRA_REVEAL_CENTER_PADDING, this);
    }


//    //监听区分searchview的返回和其他按键返回
//    @Override
//    public void onBackPressed() {
//        if (searchView.onBackPressed()) {
//
//        } else {
//            searchView.closeSearch();
//            super.onBackPressed();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (searchView.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
                return true;
            }
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Badge addBadgeAt(int position, int number) {
        // add badge
        mBadge = new QBadgeView(this)
                .setBadgeNumber(number)
                .setGravityOffset(12, 2, true)
                .bindTarget(binding.bottomNavigationView.getBottomNavigationItemView(position))
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState) {
//                            Toast.makeText(MainActivity.this, "已读", Toast.LENGTH_SHORT).show();
//                            if (binding.bottomNavigationView.getCurrentItem() == 1) {
//                                ((MyFragment) fragments.get(2)).hideBadge();
//                            } else {
//                                ((MyFragment) fragments.get(2)).setBadgeNum(0);
//                            }
                        }
                    }
                });
        return mBadge;
    }
}