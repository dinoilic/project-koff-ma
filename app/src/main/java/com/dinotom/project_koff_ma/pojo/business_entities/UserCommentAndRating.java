package com.dinotom.project_koff_ma.pojo.business_entities;

import com.google.gson.annotations.SerializedName;

public class UserCommentAndRating
{
    @SerializedName("pk")
    private Integer pk;

    @SerializedName("user_rating")
    private Integer userRating;

    @SerializedName("user_comment")
    private String userComment;

    public Integer getPk() { return pk; }
    public Integer getUserRating()
    {
        return userRating;
    }
    public String getUserComment() { return  userComment; }
}
