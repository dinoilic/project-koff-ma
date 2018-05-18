package com.dinotom.project_koff_ma.pojo.business_entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentAndRating
{

    @SerializedName("pk")
    @Expose
    private Integer pk;
    @SerializedName("user")
    @Expose
    private List<String> user = null;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getPk()
    {
        return pk;
    }

    public List<String> getUser()
    {
        return user;
    }

    public Integer getRating()
    {
        return rating;
    }

    public String getComment()
    {
        return comment;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public String getUpdatedAt()
    {
        return updatedAt;
    }
}