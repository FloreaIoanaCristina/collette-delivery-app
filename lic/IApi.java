package com.dam.lic;

import static com.dam.lic.ServerValues.CONTENT_TYPE;
import static com.dam.lic.ServerValues.SERVER_KEY;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IApi {

    @Headers ({"Authorization:"+SERVER_KEY,"Content-Type:"+CONTENT_TYPE})
    @POST("fcm/send")
    Call<PushNotification> sendNotification(@Body PushNotification pushNotification);
}
