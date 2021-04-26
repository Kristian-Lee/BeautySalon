package com.example.beautysalon.utils;

import com.example.beautysalon.ResponseCode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Lee
 * @date 2021.3.25  01:34
 * @description
 */
public class NetClient {
    private static NetClient netClient;
    private NetClient(){
        client = initOkHttpClient();
    }
    public final OkHttpClient client;
    private OkHttpClient initOkHttpClient(){
        //初始化的时候可以自定义一些参数
        return new OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)//设置读取超时为5秒
                .connectTimeout(5000, TimeUnit.MILLISECONDS)//设置链接超时为5秒
                .build();
    }
    public static NetClient getNetClient(){
        if(netClient == null){
            netClient = new NetClient();
        }
        return netClient;
    }
    public void callNet(String url, MyCallBack mCallback){
        Request request = new Request.Builder().url(url).build();
        Call call = getNetClient().initOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求网络失败，调用自己的接口，通过传回的-1可以知道错误类型
                mCallback.onFailure(ResponseCode.REQUEST_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求网络成功说明服务器有响应，但返回的是什么我们无法确定，可以根据响应码判断
                if (response.code() == 200) {
                    //如果是200说明正常，调用MyCallBack的成功方法，传入数据
                    mCallback.onResponse(response);
                }else{
                    //如果不是200说明异常，调用MyCallBack的失败方法将响应码传入
                    mCallback.onFailure(ResponseCode.SERVER_ERROR);
                }
            }
        });
    }

    public void callNet(String url, RequestBody requestBody, MyCallBack mCallback){
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = getNetClient().initOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求网络失败，调用自己的接口，通过传回的-1可以知道错误类型
                mCallback.onFailure(ResponseCode.REQUEST_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求网络成功说明服务器有响应，但返回的是什么我们无法确定，可以根据响应码判断
                if (response.code() == 200) {
                    //如果是200说明正常，调用MyCallBack的成功方法，传入数据
                    mCallback.onResponse(response);
                }else{
                    //如果不是200说明异常，调用MyCallBack的失败方法将响应码传入
                    mCallback.onFailure(ResponseCode.SERVER_ERROR);
                }
            }
        });
    }

    public void callNet(String url, String headerName, String header, RequestBody requestBody, MyCallBack mCallback){
        Request request = new Request.Builder().url(url).header(headerName, header).post(requestBody).build();
        Call call = getNetClient().initOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求网络失败，调用自己的接口，通过传回的-1可以知道错误类型
                mCallback.onFailure(ResponseCode.REQUEST_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求网络成功说明服务器有响应，但返回的是什么我们无法确定，可以根据响应码判断
                if (response.code() == 200) {
                    //如果是200说明正常，调用MyCallBack的成功方法，传入数据
                    mCallback.onResponse(response);
                }else{
                    //如果不是200说明异常，调用MyCallBack的失败方法将响应码传入
                    mCallback.onFailure(ResponseCode.SERVER_ERROR);
                }
            }
        });
    }

    public interface MyCallBack {
        //链接成功执行的方法
        void onFailure(int code);//方法参数用int数据类型，相当于是一个标识
        //链接失败执行的方法
        void onResponse(Response response) throws IOException;//方法参数根据个人需求写，可以是字符串，也可以是输入流
    }
}

