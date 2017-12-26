package com.ezly.ezly_android.Data.Response;

import com.ezly.ezly_android.Data.EzlyJob;

import java.util.ArrayList;

/**
 * Created by Johnnie on 11/10/16.
 */

public class GetJobsResponse extends EzlyBaseResponse {
    private ArrayList<EzlyJob> data;

    public ArrayList<EzlyJob> getJobs() {
        return data;
    }

    public void setJobs(ArrayList<EzlyJob> jobs) {
        this.data = jobs;
    }
}
