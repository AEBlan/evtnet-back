package com.evtnet.evtnetback.util;

import java.util.List;

import lombok.Data;

public class GeographyUtil {
    
    @Data
    public static class Location {
        double latitude;
        double longitude;
        
        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
    
    public static Location calculateCenter(List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            throw new IllegalArgumentException("Location list cannot be empty");
        }
        
        double x = 0, y = 0, z = 0;
        
        for (Location loc : locations) {
            double lat = Math.toRadians(loc.latitude);
            double lon = Math.toRadians(loc.longitude);
            
            x += Math.cos(lat) * Math.cos(lon);
            y += Math.cos(lat) * Math.sin(lon);
            z += Math.sin(lat);
        }
        
        int total = locations.size();
        x /= total;
        y /= total;
        z /= total;
        
        double centerLon = Math.atan2(y, x);
        double hyp = Math.sqrt(x * x + y * y);
        double centerLat = Math.atan2(z, hyp);
        
        return new Location(Math.toDegrees(centerLat), Math.toDegrees(centerLon));
    }
    
    /**
     * Calculate distance between two locations using Haversine formula
     * @return distance in kilometers
     */
    public static double calculateDistance(Location loc1, Location loc2) {
        final double EARTH_RADIUS_KM = 6371.0;
        
        double lat1 = Math.toRadians(loc1.latitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lon2 = Math.toRadians(loc2.longitude);
        
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
}
