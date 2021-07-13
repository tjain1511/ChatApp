package com.indianapp.chatapp.Interface;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FcmInterface {
    @POST("/")
    Call<ResponseBody> sendFcm(
            @Body HashMap<String, String> map

    );
}
