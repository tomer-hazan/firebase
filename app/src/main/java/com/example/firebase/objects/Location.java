package com.example.firebase.objects;

public class Location {
    double longitude;
    double latitude;
    public Location(double longitude, double latitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude(){
        return longitude;
    }
    public double getLatitude(){
        return latitude;
    }
    public void setLatitude(double latitude){
        this.latitude= latitude;
    }
    public void setLongitude(double longitude){
        this.longitude= longitude;
    }
    public double distance(Location other){
        return calculateDistance(this.latitude,this.longitude,other.latitude,other.longitude);
    }
    public double getMinLatBound(double tolerance){return latitude-tolerance/Math.sqrt(2);}
    public double getMaxLatBound(double tolerance){
        return latitude+tolerance/Math.sqrt(2);
    }
    public double getMinLonBound(double tolerance){return longitude-tolerance/Math.sqrt(2);}
    public double getMaxLonBound(double tolerance){
        return longitude+tolerance/Math.sqrt(2);
    }
    double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
    double calculateDistance(double startLat, double startLong, double endLat, double endLong) {
        final double EARTH_RADIUS = 6371.0;

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
