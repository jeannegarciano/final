package com.thesis.velma;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by cathlynjaneamodia on 10/2/2017.
 */

public interface ApiService {

    @GET("/velma_events.php")
    public void getMyJSON(Callback<List<EventsEntity>> response);
}