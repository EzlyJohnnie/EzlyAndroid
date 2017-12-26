package com.ezly.ezly_android.Data.Response;

import com.ezly.ezly_android.Data.EzlyJob;

/**
 * Created by Johnnie on 29/10/16.
 */

public class GetJobDetailResponse extends EzlyBaseResponse {
    private EzlyJob data;

    public EzlyJob getJob() {
        return data;
    }

    public void setJobs(EzlyJob jobs) {
        this.data = jobs;
    }
}
