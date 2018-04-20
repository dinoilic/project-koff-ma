package com.dinotom.project_koff_ma.pojo.category;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Child implements Serializable
{
    @SerializedName("pk")
    @Expose
    private Integer pk;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("image")
    @Expose
    private String image;

    public Integer getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}