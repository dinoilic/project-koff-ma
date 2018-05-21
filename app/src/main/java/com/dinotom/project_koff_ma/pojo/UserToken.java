package com.dinotom.project_koff_ma.pojo;

import com.google.gson.annotations.SerializedName;

public class UserToken
{
    @SerializedName("token")
    private String token;

    public String getToken()
    {
        return token;
    }
}

