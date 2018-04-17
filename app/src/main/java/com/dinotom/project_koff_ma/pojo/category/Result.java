package com.dinotom.project_koff_ma.pojo.category;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result
{
    @SerializedName("pk")
    @Expose
    private Integer pk;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("icon_name")
    @Expose
    private String iconName;

    @SerializedName("children")
    @Expose
    private List<Child> children = null;

    public Integer getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public String getIconName() {
        return iconName;
    }

    public List<Child> getChildren() {
        return children;
    }
}