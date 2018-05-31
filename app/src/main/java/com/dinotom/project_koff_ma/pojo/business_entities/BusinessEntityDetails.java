package com.dinotom.project_koff_ma.pojo.business_entities;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessEntityDetails {

    @SerializedName("pk")
    @Expose
    private Integer pk;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("location")
    @Expose
    private List<Double> location = null;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("working_hours")
    @Expose
    private List<DayWorkingHours> workingHours = null;
    @SerializedName("e_mail")
    @Expose
    private List<String> eMail = null;
    @SerializedName("web_site")
    @Expose
    private List<String> webSite = null;
    @SerializedName("rating")
    @Expose
    private Double rating;

    public Double getRating() {
        return rating;
    }

    public Integer getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<Double> getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public List<DayWorkingHours> getWorkingHours() {
        return workingHours;
    }

    public List<String> geteMail() {
        return eMail;
    }

    public List<String> getWebSite() {
        return webSite;
    }
}