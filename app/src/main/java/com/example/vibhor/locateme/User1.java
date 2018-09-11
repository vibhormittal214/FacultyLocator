package com.example.vibhor.locateme;

/**
 * Created by vibhor on 23-09-2017.
 */

public class User1 {
    public String name11;
    public String eid11;
    public String sub11;
    public String latitude;
    public String longitude;
    public String altitude;

    public User1() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User1(String name11, String eid11,String sub11,String latitude,String longitude,String altitude) {
        this.name11 = name11;
        this.eid11=eid11;
        this.sub11 = sub11;
        this.latitude=latitude;
        this.longitude=longitude;
        this.altitude=altitude;
    }
}
