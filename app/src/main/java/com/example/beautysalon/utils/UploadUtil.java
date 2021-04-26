package com.example.beautysalon.utils;

/**
 * @author Lee
 * @date 2021.4.13  13:02
 * @description
 */

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UploadUtil {
    private static final int IMG_SUCCESS = 2;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType FROM_DATA = MediaType.parse("multipart/form-data");
    private static final MediaType AUDIO = MediaType.parse("audio/mp3");
    private static final MediaType VIDEO = MediaType.parse("video/mp4");

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20*1000, TimeUnit.MILLISECONDS)
            .readTimeout(20*1000,TimeUnit.MILLISECONDS)
            .writeTimeout(20*1000, TimeUnit.MILLISECONDS).build();



    //上传图片与参数
    public static String upload(String url, Map<String,String> params, String filepath) throws IOException {
        OkHttpClient client = new OkHttpClient();

        File file = new File(filepath);

        MultipartBody.Builder mbuilder = new MultipartBody.Builder();
        if(params != null && params.size() > 0) {
            for (String param : params.keySet()) {
                mbuilder.addFormDataPart(param,params.get(param));
            }
        }
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG,file);
        RequestBody requestBody = mbuilder
                .setType(MultipartBody.ALTERNATIVE)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        //Response response = client.newCall(request).execute();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            return result;
        }
    }
}