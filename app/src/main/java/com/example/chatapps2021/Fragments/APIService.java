package com.example.chatapps2021.Fragments;

import com.example.chatapps2021.Notifications.MyResponse;
import com.example.chatapps2021.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAzT0q5gw:APA91bFg3fS9phh2vagEXHfRRb9rL5rD_uESfeL13rDUHgMRCSWbLZwPf9j9R8kLMGi6dJpeBizq4WDltM1FEXjMxeEUa22omPi18VXFXGFBHgndm-YGIn8Zbx2xZ990aYPX28QWCNBy"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotificaton(@Body Sender body);

}
