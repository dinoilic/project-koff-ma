package com.dinotom.project_koff_ma.pojo.category;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result
{

    @SerializedName("pk")
    @Expose
    public Integer pk;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("children")
    @Expose
    public List<Child> children = null;

}