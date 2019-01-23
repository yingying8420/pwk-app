package com.app.util;

import com.app.service.DataService;

import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit {
  private DataService service;

  /**
   * 获取Retrofit实例
   * @return
   */
  public static Retrofit getRetrofit(){
    return new Retrofit();
  }

  private Retrofit() {
    retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
            .baseUrl("http://192.168.1.146:8181/drainOutlet/")
            //.addConverterFactory(CustomConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    service = retrofit.create(DataService.class);
  }

  /**
   * 获取IBeanService实例
   * @return
   */
  public DataService getService(){
    return service;
  }
}
