package com.dinotom.project_koff_ma;

import com.dinotom.project_koff_ma.pojo.UserToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface
{
    @FormUrlEncoded
    @POST("api-token-auth/")
    Call<UserToken> getUserToken(@Field("username") String username, @Field("password") String password);
}
