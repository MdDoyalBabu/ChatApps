package com.example.chatapps2021.Notifications;

import com.example.chatapps2021.Fragments.APIService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static Retrofit retrofit=null;

    public static Retrofit getClient(String url){

        if (retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
