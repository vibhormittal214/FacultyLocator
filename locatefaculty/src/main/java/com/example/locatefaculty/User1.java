package com.example.locatefaculty;

/**
 * Created by vibhor on 24-09-2017.
 */
public class User1 {
    public String name11;
    public String eid11;

    public User1() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User1(String name11, String eid11) {
        this.name11 = name11;
        this.eid11=eid11;
    }
}