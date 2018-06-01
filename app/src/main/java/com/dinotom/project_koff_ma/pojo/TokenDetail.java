package com.dinotom.project_koff_ma.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenDetail
{
    @SerializedName("key")
    @Expose
    private String key;

    @SerializedName("user")
    @Expose
    private User user;

    public String getKey() {
        return key;
    }

    public User getUser() {
        return user;
    }
}

