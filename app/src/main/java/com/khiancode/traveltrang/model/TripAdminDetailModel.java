package com.khiancode.traveltrang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TripAdminDetailModel {

    @SerializedName("trip")
    @Expose
    private TripAdminModel trip;

    @SerializedName("listtravel")
    @Expose
    private ArrayList<ListTravelTripAdminModel> listtravel;

    public TripAdminModel getTrip() {
        return trip;
    }

    public void setTrip(TripAdminModel trip) {
        this.trip = trip;
    }

    public ArrayList<ListTravelTripAdminModel> getListtravel() {
        return listtravel;
    }

    public void setListtravel(ArrayList<ListTravelTripAdminModel> listtravel) {
        this.listtravel = listtravel;
    }

}