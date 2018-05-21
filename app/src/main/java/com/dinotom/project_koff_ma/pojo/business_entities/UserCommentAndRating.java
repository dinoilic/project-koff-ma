package com.dinotom.project_koff_ma.pojo.business_entities;

import com.google.gson.annotations.SerializedName;

public class UserCommentAndRating
{
    @SerializedName("user_rating")
    private Integer userRating;

    @SerializedName("user_comment")
    private String userComment;

    public Integer getUserRating()
    {
        return userRating;
    }
    public String getUserComment() { return  userComment; }
}
