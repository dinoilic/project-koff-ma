package com.dinotom.project_koff_ma.pojo.business_entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessEntity
{
    @SerializedName("pk")
    @Expose
    private Integer pk;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("location")
    @Expose
    private List<Double> location = null;
    @SerializedName("avg_rating")
    @Expose
    private Double avgRating;
    @SerializedName("working_hours")
    @Expose
    private List<DayWorkingHours> workingHours = null;

    public Integer getPk()
    {
        return pk;
    }

    public String getName()
    {
        return name;
    }

    public String getAddress()
    {
        return address;
    }

    public Double getDistance()
    {
        return distance;
    }

    public List<Double> getLocation()
    {
        return location;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public List<DayWorkingHours> getWorkingHours()
    {
        return workingHours;
    }
}