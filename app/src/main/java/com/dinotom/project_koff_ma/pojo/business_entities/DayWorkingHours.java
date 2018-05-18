package com.dinotom.project_koff_ma.pojo.business_entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DayWorkingHours
{
    @SerializedName("name")
    @Expose
    private String dayShortName;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;

    public String getDayShortName() {
        return dayShortName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
