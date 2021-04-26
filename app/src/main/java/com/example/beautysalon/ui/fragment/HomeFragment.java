package com.example.beautysalon.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.ScreenUtil;
import com.example.beautysalon.TuchongEntity;
import com.example.beautysalon.dao.EvaluationDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.databinding.FragmentHomeBinding;
import com.example.beautysalon.ui.activity.CouponReceiveActivity;
import com.example.beautysalon.ui.activity.SignInActivity;
import com.example.beautysalon.ui.activity.TopUpActivity;
import com.example.beautysalon.ui.adapter.DemoAdapter;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.google.gson.Gson;
import com.luck.picture.lib.tools.ToastUtils;
import com.stx.xhb.androidx.XBanner;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private XBanner mBanner4;
    private FragmentHomeBinding mBinding;
    private LinearLayout mCouponContainer, mPointsContainer, mTopUpContainer;
    private UserDao mUser;
    private View mHeadView;
    private Bundle mBundle = new Bundle();
    private SimpleSearchView mSearchView;
    private static HomeFragment mInstance;
    private HashMap<String, List> mMap;
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private List<StylistDao> mStylistDaoList;
    private List<EvaluationDao> mEvaluationDaoList;
    private boolean mIsOk = false; // 是否完成View初始化
    private boolean mIsFirst = true; // 是否为第一次加载
    public static synchronized HomeFragment getInstance() {
        if (mInstance == null) {
            mInstance = new HomeFragment();
        }
        return mInstance;
    }

    public HomeFragment() {
    }

    public static HomeFragment newInstance(Bundle bundle) {
        HomeFragment fragment = getInstance();
        fragment.setArguments(bundle);
        return fragment;
    }
   @Override
    public void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(inflater);
        mIsOk = true;
        initData();
        initBanner(mBanner4);
        setListeners();
        return mBinding.getRoot();
    }
    private void initBanner(XBanner banner) {
        //设置广告图片点击事件
        banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                LogUtils.i("click pos:" + position);
                ToastUtils.s(getActivity(), ("点击了第" + (position + 1) + "图片"));
            }
        });
        //加载广告图片
        banner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                //此处适用Fresco加载网络图片，可自行替换自己的图片加载框架
                SimpleDraweeView draweeView = (SimpleDraweeView) view;
                TuchongEntity.FeedListBean.EntryBean listBean = ((TuchongEntity.FeedListBean.EntryBean) model);
                String url = "https://photo.tuchong.com/" + listBean.getImages().get(0).getUser_id() + "/f/" + listBean.getImages().get(0).getImg_id() + ".jpg";
                draweeView.setImageURI(Uri.parse(url));
//                加载本地图片展示
//                ((ImageView) view).setImageResource(((LocalImageInfo) model).getXBannerUrl());
            }
        });
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.i("onPageSelected===>", i + "");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    void setListeners() {
        mCouponContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CouponReceiveActivity.class);
                Bundle bundle = getActivity().getIntent().getExtras();
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mPointsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                Bundle bundle = getActivity().getIntent().getExtras();
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mTopUpContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TopUpActivity.class);
                Bundle bundle = getActivity().getIntent().getExtras();
                UserDao userDao = (UserDao) bundle.getSerializable("user");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    requestForData();
                    //刷新完成
                    mBinding.swipeLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "已更新", Toast.LENGTH_SHORT).show();
                }, 400);
            }
        });
//        mBinding.recyclerView.setLoadingMoreEnabled(true);
//        mBinding.recyclerView.setLoadingMoreProgressStyle(ProgressStyle.LineScale);
//        mBinding.recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        requestForData();
//                        mBinding.recyclerView.refreshComplete();
//                    }
//                },400);
//            }
//
//            @Override
//            public void onLoadMore() {
//                mBinding.recyclerView.loadMoreComplete();
//            }
//        });
//        mSearchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                List<StylistDao> stylistDaoList = new ArrayList<>();
//                List<EvaluationDao> evaluationDaoList = new ArrayList<>();
//                for (int i = 0; i < mStylistDaoList.size(); i++) {
//                    if (mStylistDaoList.get(i).getRealName().contains(query)) {
//                        stylistDaoList.add(mStylistDaoList.get(i));
//                        evaluationDaoList.add(mEvaluationDaoList.get(i));
//                    }
//                }
//                if (stylistDaoList.size() <= 0) {
//                    Toast.makeText(getActivity(), "没有找到符合要求的发型师数据", Toast.LENGTH_SHORT).show();
//                    loadData(mStylistDaoList, mEvaluationDaoList, mUser.getUserId());
//                } else {
//                    loadData(stylistDaoList, evaluationDaoList, mUser.getUserId());
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                List<StylistDao> stylistDaoList = new ArrayList<>();
//                List<EvaluationDao> evaluationDaoList = new ArrayList<>();
//                for (int i = 0; i < mStylistDaoList.size(); i++) {
//                    if (mStylistDaoList.get(i).getRealName().contains(newText)) {
//                        stylistDaoList.add(mStylistDaoList.get(i));
//                        evaluationDaoList.add(mEvaluationDaoList.get(i));
//                    }
//                }
//                loadData(stylistDaoList, evaluationDaoList, mUser.getUserId());
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextCleared() {
//                return false;
//            }
//        });
    }

    /**
     * 初始化数据
     */
    private void requestForData() {
        NetClient.getNetClient().callNet(NetworkSettings.STYLIST_DATA, new NetClient.MyCallBack() {
            @Override
            public void onFailure(int code) {
                mMessage.what = ResponseCode.REQUEST_STYLIST_DATA_FAILED;
                mHandler.post(()-> Utils.showMessage(getActivity(), mMessage));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                        mMessage.what = restResponse.getCode();
                        if (mMessage.what == ResponseCode.REQUEST_STYLIST_DATA_SUCCESS) {
                            mMap = JSON.parseObject(restResponse.getData().toString(), HashMap.class);
                            mStylistDaoList = Utils.jsonToList(StylistDao.class, mMap.get("stylist"));
                            mEvaluationDaoList = Utils.jsonToList(EvaluationDao.class, mMap.get("evaluation"));
                            Collections.reverse(mStylistDaoList);
                            Collections.reverse(mEvaluationDaoList);
                            mHandler.post(() -> {
                                loadData(mStylistDaoList, mEvaluationDaoList, mUser.getUserId());
                            });
                        }
                    } else {
                        System.out.println("失败了啊");
                    }
                }
            }
        });



        //加载网络图片资源
        String url = "https://api.tuchong.com/2/wall-paper/app";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getActivity(), "加载广告数据失败", Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onResponse(String response, int id) {
                        TuchongEntity advertiseEntity = new Gson().fromJson(response, TuchongEntity.class);
                        List<TuchongEntity.FeedListBean> others = advertiseEntity.getFeedList();
                        List<TuchongEntity.FeedListBean.EntryBean> data = new ArrayList<>();
                        for (int i = 0; i < others.size(); i++) {
                            TuchongEntity.FeedListBean feedListBean = others.get(i);
                            if ("post".equals(feedListBean.getType())) {
                                data.add(feedListBean.getEntry());
                            }
                        }


                        //刷新数据之后，需要重新设置是否支持自动轮播
                        mBanner4.setAutoPlayAble(data.size() > 1);
                        mBanner4.setIsClipChildrenMode(true);
                        mBanner4.setBannerData(R.layout.layout_fresco_imageview, data);
                        mBanner4.getViewPager().setOffscreenPageLimit(1);

                    }
                });
    }

    private void initData() {
        mUser = (UserDao) getArguments().getSerializable("user");
        mBinding.swipeLayout.setColorSchemeColors(Color.parseColor("#5872fd"));
        mHeadView = getLayoutInflater().inflate(R.layout.fragment_home_head_view, null);
        mCouponContainer = mHeadView.findViewById(R.id.coupon_container);
        mPointsContainer = mHeadView.findViewById(R.id.points_container);
        mTopUpContainer = mHeadView.findViewById(R.id.top_up_container);
        mBanner4 = mHeadView.findViewById(R.id.banner4);
        mSearchView = getActivity().findViewById(R.id.searchView);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtil.getScreenWidth(getContext()) / 2);
        mBanner4.setLayoutParams(layoutParams);
        if (mIsOk && mIsFirst) { // 加载数据时判断是否完成view的初始化，以及是不是第一次加载此数据
            requestForData();
            mIsFirst = false; // 加载第一次数据后改变状态，后续不再重复加载
        } else {
            loadData(mStylistDaoList, mEvaluationDaoList, mUser.getUserId());
        }
    }
    void loadData(List<StylistDao> stylistDaos, List<EvaluationDao> evaluationDaos, Integer userId) {
        DemoAdapter adapter = new DemoAdapter(getActivity(), stylistDaos, evaluationDaos, userId);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view, null);
        adapter.setEmptyView(emptyView);
        if ((ViewGroup) mHeadView.getParent() != null) {
            ((ViewGroup) mHeadView.getParent()).removeView(mHeadView);
        }
        adapter.setHeaderView(mHeadView);
        mBinding.recyclerView.setAdapter(adapter);
    }
}