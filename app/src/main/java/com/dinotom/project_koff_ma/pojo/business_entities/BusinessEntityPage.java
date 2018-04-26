package com.dinotom.project_koff_ma.pojo.business_entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessEntityPage
{
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("next")
    @Expose
    private String next;
    @SerializedName("previous")
    @Expose
    private String previous;
    @SerializedName("results")
    @Expose
    private List<BusinessEntity> results = null;

    public Integer getCount()
    {
        return count;
    }

    public String getNext()
    {
        return next;
    }

    public Object getPrevious()
    {
        return previous;
    }

    public List<BusinessEntity> getResults()
    {
        return results;
    }
}
