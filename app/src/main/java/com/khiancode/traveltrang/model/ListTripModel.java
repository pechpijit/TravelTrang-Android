package com.khiancode.traveltrang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListTripModel {

    @SerializedName("travellist")
    @Expose
    private boolean travellist;

    @SerializedName("trip")
    @Expose
    private TripModel trip;

    @SerializedName("travel")
    @Expose
    private TravelModel travel;

    public boolean isTravellist() {
        return travellist;
    }

    public void setTravellist(boolean travellist) {
        this.travellist = travellist;
    }

    public TripModel getTrip() {
        return trip;
    }

    public void setTrip(TripModel trip) {
        this.trip = trip;
    }

    public TravelModel getTravel() {
        return travel;
    }

    public void setTravel(TravelModel travel) {
        this.travel = travel;
    }
}