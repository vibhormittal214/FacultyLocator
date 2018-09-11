package com.example.vibhor.locateme;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.load.data.StreamLocalUriFetcher;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by vibhor on 23-09-2017.
 */

public class LocationService extends Service{
    Timer timer=new Timer();
    long UPDATE_INTERVAL=6000;
    public static final String Stub = null;
    LocationManager mlocmag;
    LocationListener mlocList;
    private double lat, longn,alti;
    private FirebaseAuth firebaseAuth;
    public DatabaseReference current_user_db, newRef;// current_user_db1, newRef1;
    String user_id;
    int count=0;
    double loc_count=0;
    Location loc;
    int s=60000;
    String eid1,sub1,name1;
    public static String str_receiver = "com.example.vibhor.locateme.receiver";
    Intent intent;
    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        user_id = firebaseAuth.getCurrentUser().getUid();
        current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child("teacher");
        intent = new Intent(str_receiver);
        mlocmag = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocList = new MyLocationList();
        //current_user_db1 = FirebaseDatabase.getInstance().getReference().child("users").child("teacherdata").child(eid1);
      /*  loc = mlocmag.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc==null) {
            loc = mlocmag.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }// location.
            UpdateWithNewLocation(loc); // This method is used to get updated
            mlocmag.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, mlocList);*/
        try {
            loc = mlocmag.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc==null) {
                loc = mlocmag.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }// location.
            UpdateWithNewLocation(); // This method is used to get
            mlocmag.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0,0,
                    mlocList);
        } catch (java.lang.SecurityException ex) {

        } catch (IllegalArgumentException ex) {

        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        mlocmag.removeUpdates(mlocList);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        //eid1= intent.getStringExtra("test");
        return START_NOT_STICKY;
    }


    private void UpdateWithNewLocation() {
       // UPDATE_INTERVAL = 1200000000;

      timer.scheduleAtFixedRate(new TimerTask() {
            private Handler updateUI = new Handler() {
                @Override
                public void dispatchMessage(Message msg) {
                    super.dispatchMessage(msg);
                   //
                    updateFireBase();
                    //updateFireBase1();

                }
            };

            @Override
            public void run() {
                if (loc != null) {
                    final double latitude = loc.getLatitude(); // Updated lat
                    final double longitude = loc.getLongitude(); // Updated long
                    final double altitude = loc.getAltitude();
                    lat = latitude;
                    longn = longitude;
                    alti=altitude;
                    updateUI.sendEmptyMessage(0);
                    fn_update(loc);

                }


            }
        },0,30000);
   }


    private void updateFireBase() {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        newRef = current_user_db.child(currentUserID);
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    eid1 = String.valueOf(snapshot.child("eid11").getValue());
                    name1 = String.valueOf(snapshot.child("name11").getValue());
                    sub1 = String.valueOf(snapshot.child("sub11").getValue());
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child("teacherdata").child(eid1);
                    databaseReference.child("latitude").setValue(lat+"");
                    databaseReference.child("longitude").setValue(longn+"");
                    databaseReference.child("altitude").setValue((loc_count/count)+"");
                    newRef.child("latitude").setValue(lat + "");
                    newRef.child("longitude").setValue(longn + "");
                    newRef.child("altitude").setValue((loc_count/count) + "");
                    loc_count=0;
                    count=0;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

    }

   /* private void updateFireBase1(){

        Toast.makeText(getApplicationContext(),eid1,Toast.LENGTH_SHORT).show();
        current_user_db1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {

                    current_user_db1.child("latitude").setValue(lat + "");
                    current_user_db1.child("longitude").setValue(longn + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
    }*/
    private void fn_update(Location location){

        intent.putExtra("latutide",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");
        intent.putExtra("altitude",(loc_count/count)+"");
        sendBroadcast(intent);

    }


    public class MyLocationList implements LocationListener {

        public void onLocationChanged(Location arg0) {
            loc=arg0;
            loc_count=loc_count+loc.getLatitude();
            count++;
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS Disable ",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS enabled",
                    Toast.LENGTH_LONG).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }
}