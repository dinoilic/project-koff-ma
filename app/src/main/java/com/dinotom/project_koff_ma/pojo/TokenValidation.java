package com.dinotom.project_koff_ma.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenValidation
{
    @SerializedName("detail")
    @Expose
    private String detail;

    public boolean isValid()
    {
        if(detail.equals("Invalid token."))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
