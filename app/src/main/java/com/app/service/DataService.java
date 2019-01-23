package com.app.service;

import com.app.util.Javabean;
import com.app.util.Outlet;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface DataService {

    @FormUrlEncoded
    @POST("app/login1")
    Call<Javabean> addReviews(@FieldMap Map<String, String> fields);

    //添加排污口信息
    @POST("outletApp/outlet/add")
    Call<Javabean> addOutlet(@Body Outlet outlet, @Query("token") String token);

    //修改排污口信息
    @PUT("outletApp/outlet/updateoutlet")
    Call<Javabean> updateOutlet(@Body Outlet outlet, @Query("token") String token);

    //登录
    @GET("app/login")
    Call<Javabean> login(@Query("username") String username, @Query("password") String password);

    //字典项
    @GET("app/zdx/dictCode")
    Call<Javabean> getDictCode(@Query("token") String token, @Query("dictCode") String dictCode);

    //河流
    @GET("app/river/RiverSeachItem")
    Call<Javabean> getRiver(@Query("token") String token);

    //排污口查询
    @GET("outletApp/outlet/getoutlet")
    Call<Javabean> getOutlet(@Query("token") String token, @Query("riverid") String riverid);

}
