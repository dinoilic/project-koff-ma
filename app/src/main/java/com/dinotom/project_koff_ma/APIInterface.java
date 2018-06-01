package com.dinotom.project_koff_ma;

import com.dinotom.project_koff_ma.pojo.TokenDetail;
import com.dinotom.project_koff_ma.pojo.TokenValidation;
import com.dinotom.project_koff_ma.pojo.UserPk;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntityPage;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntityDetails;
import com.dinotom.project_koff_ma.pojo.business_entities.CommentAndRating;
import com.dinotom.project_koff_ma.pojo.business_entities.CommentAndRatingPage;
import com.dinotom.project_koff_ma.pojo.business_entities.PostCommentAndRating;
import com.dinotom.project_koff_ma.pojo.business_entities.UserCommentAndRating;
import com.dinotom.project_koff_ma.pojo.category.Category;
import com.dinotom.project_koff_ma.pojo.UserToken;
import com.dinotom.project_koff_ma.pojo.search.SearchPage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface
{
    @FormUrlEncoded
    @POST("api-token-auth/")
    Call<UserToken> getUserToken(@Field("username") String username, @Field("password") String password);

    @GET("api/v1/get-user-pk/")
    Call<UserPk> getUserPk();

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

    @GET("api/v1/entities/")
    Call<BusinessEntityPage> getBusinessEntities(@Query("subcategory") Integer pk,
                                                 @Query("location") String location,
                                                 @Query("radius") Double radius,
                                                 @Query("is_working") Integer is_working,
                                                 @Query("sort") String sort_mode,
                                                 @Query("page") Integer page,
                                                 @Query("ids") String ids);

    @GET("api/v1/entities/")
    Call<BusinessEntityPage> getBusinessEntities(@Query("location") String location,
                                                 @Query("radius") Double radius,
                                                 @Query("is_working") Integer is_working,
                                                 @Query("sort") String sort_mode,
                                                 @Query("page") Integer page,
                                                 @Query("ids") String ids);

    @GET("api/v1/entities/{id}/")
    Call<BusinessEntityDetails> getBusinessEntityDetails(@Path("id") Integer id);

    @GET("api/v1/get-user-comment-and-rating/")
    Call<UserCommentAndRating> getUserRatingAndComment(@Query("entity") Integer pk);

    @GET("api/v1/ratings-and-comments/")
    Call<CommentAndRatingPage> getRatingsAndComments(@Query("entity") Integer pk,
                                                     @Query("page") Integer page);

    @FormUrlEncoded
    @POST("api/v1/ratings-and-comments-list/")
    Call<PostCommentAndRating> postCommentAndRating(@Field("entity") Integer entityPk,
                                                    @Field("rating") Integer rating,
                                                    @Field("comment") String comment);

    @FormUrlEncoded
    @PATCH("api/v1/ratings-and-comments/{pk}/")
    Call<ResponseBody> updateRating(@Path("pk") Integer pk, @Field("rating") Integer rating);

    @FormUrlEncoded
    @PATCH("api/v1/ratings-and-comments/{pk}/")
    Call<ResponseBody> updateComment(@Path("pk") Integer pk, @Field("comment") String comment);

    @FormUrlEncoded
    @PATCH("api/v1/ratings-and-comments/{pk}/")
    Call<ResponseBody> updateCommentAndRating(@Path("pk") Integer pk,
                                              @Field("rating") Integer rating,
                                              @Field("comment") String comment);

    @DELETE("api/v1/ratings-and-comments/{pk}/")
    Call<ResponseBody> deleteCommentAndRating(@Path("pk") Integer pk);

    @GET("api/v1/entities/search/")
    Call<SearchPage> getSearchPage(@Query("description") String description);

    @GET("api/v1/token/{token}/")
    Call<TokenDetail> getTokenDetail(@Path("token") String token);
}
