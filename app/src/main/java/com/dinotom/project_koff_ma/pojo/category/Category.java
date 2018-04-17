package com.dinotom.project_koff_ma.pojo.category;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category
{
    @SerializedName("count")
    @Expose
    private Integer count;

    @SerializedName("next")
    @Expose
    private Object next;

    @SerializedName("previous")
    @Expose
    private Object previous;

    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    public Integer getCount() {
        return count;
    }

    public Object getNext() {
        return next;
    }

    public Object getPrevious() {
        return previous;
    }

    public List<Result> getResults() {
        return results;
    }
}


