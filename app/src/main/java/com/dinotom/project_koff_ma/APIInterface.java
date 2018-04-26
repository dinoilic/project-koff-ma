package com.dinotom.project_koff_ma;

import com.dinotom.project_koff_ma.pojo.TokenValidation;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntityPage;
import com.dinotom.project_koff_ma.pojo.category.Category;
import com.dinotom.project_koff_ma.pojo.UserToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface
{
    @FormUrlEncoded
    @POST("api-token-auth/")
    Call<UserToken> getUserToken(@Field("username") String username, @Field("password") String password);

    @GET("api/v1/categories/")
    Call<Category> getMainCategories();

    @GET("api/v1/token-validation/")
    Call<TokenValidation> getTokenValidityStatus(@Header("Authorization") String token);

    @GET("api/v1/entities/")
    Call<BusinessEntityPage> getBusinessEntities(@Query("subcategory") Integer pk,
                                                 @Query("location") String location,
                                                 @Query("radius") Double radius,
                                                 @Query("is_working") Integer is_working,
                                                 @Query("sort") String sort_mode,
                                                 @Query("page") Integer page);
}
