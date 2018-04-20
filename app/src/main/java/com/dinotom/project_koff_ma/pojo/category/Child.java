package com.dinotom.project_koff_ma.pojo.category;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Child
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

    @SerializedName("children")
    @Expose
    private List<Object> children = null;

    public Integer getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public List<Object> getChildren() {
        return children;
    }


}