package com.example.beautysalon.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautysalon.dao.InformationDao;
import com.example.beautysalon.databinding.ActivityInformationDetailBinding;
import com.gyf.immersionbar.ImmersionBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InformationDetailActivity extends AppCompatActivity {

    private ActivityInformationDetailBinding mBinding;
    private InformationDao mInformationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityInformationDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        setListeners();
        show(mInformationDao.getWebView());
    }


    private void show(String text) {
        String str = "<p>据多家美媒消息，五年前的今天，也就是2012年12月6日，湖人名宿科比-布莱恩特迎来个人生涯一个非常大的里程碑!他成为NBA历史上第5位总得分达到30000分的球员，也成了历史最年轻的30000分先生。</p><p><img src=\"http://p4.qhimg.com/t0143c3caa0f210471d.jpg?size=960x960\" class=\"mCS_img_loaded\"></p><p>在湖人以103-87战胜黄蜂的比赛中，科比出战33分钟，17投10中，得到29分6篮板4助攻。其中，第二节比赛他用一记抛投让自己的职业生涯总得分达到30000分。</p><p>科比在这一天是34岁104天，超越了张伯伦(35岁179天)成为NBA历史上最年轻的30000分先生。</p><p><img src=\"http://p6.qhimg.com/t018adb2d64db452679.jpg?size=1024x576\"class=\"mCS_img_loaded\"></p><p>詹姆斯目前32岁，如无意外的话，他在本赛季(生涯第15个赛季)就能超越科比，成为NBA历史最年轻的30000分先生。</p><p><img src=\"http://p2.qhimg.com/t0111fe70b44cb3e393.jpg?size=1024x770\"class=\"mCS_img_loaded\"></p><p><img src=\"http://p0.qhimg.com/t011f7a164e5cd0d6f1.jpg?size=858x572\" class=\"mCS_img_loaded\"></p><p>返回搜狐，查看更多</p><p>责任编辑:</p>";

//        String str = "\"<p>据多家美媒消息，五年前的今天，也就是2012年12月6日，湖" +
//                "人名宿科比-布莱恩特迎来个人生涯一个非常大的里程碑!他成为NBA历史上" +
//                "第5位总得分达到30000分的球员，也成了历史最年轻的30000分先生。</p>" +
//                "<p><img src=\"http://p4.qhimg.com/t0143c3caa0f210471d.jpg?size=960" +
//                "x960\"\" class=\"mCS_img_loaded\"></p><p>在湖人以103-87战胜黄蜂的比赛" +
//                "中，科比出战33分钟，17投10中，得到29分6篮板4助攻。其中，第二节比赛他用" +
//                "一记抛投让自己的职业生涯总得分达到30000分。</p><p>科比在这一天是34岁104" +
//                "天，超越了张伯伦(35岁179天)成为NBA历史上最年轻的30000分先生。</p><p>" +
//                "<img src=\"http://p6.qhimg.com/t018adb2d64db452679.jpg?size=1024x576\"" +
//                " class=\"mCS_img_loaded\"></p><p>詹姆斯目前32岁，如无意外的话，他在本赛" +
//                "季(生涯第15个赛季)就能超越科比，成为NBA历史最年轻的30000分先生。</p><p>" +
//                "<img src=\"http://p2.qhimg.com/t0111fe70b44cb3e393.jpg?size=1024x770\"" +
//                " class=\"mCS_img_loaded\"></p><p><img src=\"http://p0.qhimg.com/t011f7" +
//                "a164e5cd0d6f1.jpg?size=858x572\" class=\"mCS_img_loaded\"></p><p>返回搜" +
//                "狐，查看更多</p><p>责任编辑:</p>\"";

        String str1 = "\"<p>据多家美媒消息，五年前的今天，也就是2012年12月6日，湖人名宿科比-布莱恩特迎来个人生涯一个非常大的里程碑!他成为NBA历史上第5位总得分达到30000分的球员，也成了历史最年轻的30000分先生。</p><p><img src=\"http://p4.qhimg.com/t0143c3caa0f210471d.jpg?size=960x960\"\" class=\"mCS_img_loaded\"></p><p>在湖人以103-87战胜黄蜂的比赛中，科比出战33分钟，17投10中，得到29分6篮板4助攻。其中，第二节比赛他用一记抛投让自己的职业生涯总得分达到30000分。</p><p>科比在这一天是34岁104天，超越了张伯伦(35岁179天)成为NBA历史上最年轻的30000分先生。</p><p><img src=\"http://p6.qhimg.com/t018adb2d64db452679.jpg?size=1024x576\"class=\"mCS_img_loaded\"></p><p>詹姆斯目前32岁，如无意外的话，他在本赛季(生涯第15个赛季)就能超越科比，成为NBA历史最年轻的30000分先生。</p><p><img src=\"http://p2.qhimg.com/t0111fe70b44cb3e393.jpg?size=1024x770\"class=\"mCS_img_loaded\"></p><p><img src=\"http://p0.qhimg.com/t011f7a164e5cd0d6f1.jpg?size=858x572\" class=\"mCS_img_loaded\"></p><p>返回搜狐，查看更多</p><p>责任编辑:</p>\"";
        Document parse = Jsoup.parse(text);
        Elements imgs = parse.getElementsByTag("img");
        if (!imgs.isEmpty()) {
            for (Element e : imgs) {
                imgs.attr("width", "100%");
                imgs.attr("height", "auto");

            }
        }

        String content = parse.toString();
        mBinding.webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        mInformationDao = (InformationDao) getIntent().getExtras().get("information");
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //状态栏沉浸
        ImmersionBar.with(this)
                .statusBarView(mBinding.view)
                .init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        mBinding.webView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult hitTestResult = ((WebView) v)
                        .getHitTestResult();
                if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE
                        || hitTestResult.getType() == WebView.HitTestResult.IMAGE_ANCHOR_TYPE
                        || hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    Toast.makeText(InformationDetailActivity.this, "图片地址：" + hitTestResult.getExtra(), Toast.LENGTH_SHORT).show();
                    Log.e("图片保存", "保存这个图片！"
                            + hitTestResult.getExtra());
                    //跳转网页打开图片
                    Uri uri = Uri.parse(hitTestResult.getExtra());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                }
                return true;
            }
        });
        mBinding.webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                float x = 0, y = 0;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP://算是点击事件
                        Log.e("time-------", (event.getEventTime() - event.getDownTime()) + "");
                        if ((event.getEventTime() - event.getDownTime()) < 100) {
                            if (x - event.getX() < 5 && y - event.getY() < 5) {
                                WebView.HitTestResult hitTestResult = ((WebView) v)
                                        .getHitTestResult();
                                if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE
                                        || hitTestResult.getType() == WebView.HitTestResult.IMAGE_ANCHOR_TYPE
                                        || hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                                    Log.e("单机图片地址------", "保存这个图片！"
                                            + hitTestResult.getExtra());
                                    Toast.makeText(
                                            InformationDetailActivity.this,
                                            "保存这个图片！" + hitTestResult.getExtra(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
                return false;
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
}
