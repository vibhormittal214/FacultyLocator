package com.example.vibhor.locateme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

/**
 * Created by vibhor on 23-09-2017.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

        //firebase auth object
        private FirebaseAuth firebaseAuth;

        //view objects
        private TextView textViewUsername;
    private TextView textViewUsersub;
       // private Button buttonLogout;

    //LOCATION THINGS
       Button btn_start,btn_stop;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    TextView tv_latitude, tv_longitude,tv_altitude;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Geocoder geocoder;
    double latitude,longitude,altitude;
    //LOCATION THINGS ENDS
    String name,sub,id;
    public  DatabaseReference current_user_db,current_user_db1;
    String user_id;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);
            //initializing firebase authentication object
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            //if the user is not logged in
            //that means current user will return null
            if (firebaseAuth.getCurrentUser() == null) {
                //closing this activity
                finish();
                //starting login activity
                startActivity(new Intent(this, LoginActivity.class));
            }
            //getting current user
            user_id = firebaseAuth.getCurrentUser().getUid();
            current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child("teacher");

            //initializing views
            textViewUsername = (TextView) findViewById(R.id.textViewUsername);
            textViewUsersub = (TextView) findViewById(R.id.textViewUsersub);

            //buttonLogout = (Button) findViewById(R.id.buttonLogout);
            //displaying logged in user name
         //  textViewUserEmail.setText("Welcome " + user.getEmail());
            //adding listener to button
            //buttonLogout.setOnClickListener(this);

            //LOCATION THINGS
            btn_start = (Button) findViewById(R.id.btn_start);
            btn_stop = (Button) findViewById(R.id.btn_stop);
            tv_latitude = (TextView) findViewById(R.id.tv_latitude);
            tv_longitude = (TextView) findViewById(R.id.tv_longitude);
            tv_altitude=(TextView)findViewById(R.id.tv_altitude);
            geocoder = new Geocoder(this, Locale.getDefault());
            mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            medit = mPref.edit();
            //START TAKING LOCATION
            btn_stop.setOnClickListener(this);
            btn_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (boolean_permission) {
                        if (mPref.getString("service", "").matches("")) {
                            medit.putString("service", "service").commit();
                            Intent intent = new Intent(getApplicationContext(), LocationService.class);
                            startService(intent);

                        } else {
                            Intent intent = new Intent(getApplicationContext(), LocationService.class);
                            startService(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
                    }


                    }
            });
            fn_permission();
            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference newRef = current_user_db.child(currentUserID);
            newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    name = String.valueOf(dataSnapshot.child("name11").getValue());
                    id = String.valueOf(dataSnapshot.child("eid11").getValue());
                     sub= String.valueOf(dataSnapshot.child("sub11").getValue());
                    textViewUsername.setText("Welcome " + name+"  "+id+" :)");
                    textViewUsersub.setText("Your subject is  " +sub);
                   // Toast.makeText(ProfileActivity.this,name,Toast.LENGTH_LONG).show();
                    /*email = String.valueOf(dataSnapshot.child("email").getValue());
                    username = String.valueOf(dataSnapshot.child("/username").getValue());
                    ID = String.valueOf(dataSnapshot.child("uid").getValue());

                    mCurrentUser.setName(name);
                    mCurrentUser.setUsername(username);
                    mCurrentUser.setEmail(email);
                    mCurrentUser.setUid(ID);

                    selectedUsers.add(mCurrentUser);*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this,"Cancelled",Toast.LENGTH_SHORT).show();
                }
            });
           /* ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                                       showData(dataSnapshot);
                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(ProfileActivity.this,"Cancelled",Toast.LENGTH_SHORT).show();
                    // ...
                }
            };current_user_db.addValueEventListener(postListener);*/
            //START TAKIN LOCATION ENDS

        }
   /* private void showData(DataSnapshot dataSnapshot){
        User2 user= dataSnapshot.getValue(User2.class);
            name=user.getName();
            Toast.makeText(ProfileActivity.this,name,Toast.LENGTH_LONG).show();

    }*/




    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_red1:
                //logging out the user
                firebaseAuth.signOut();
                //closing activity
                finish();
                //starting login activity
                stopService(new Intent(getBaseContext(),LocationService.class));
                startActivity(new Intent(this, LoginActivity.class));

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //LOCATION PERMISSIONS
    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    //LOCATION PERMISSIONS ENDS

    //SET LOCATIONS ON PAGE
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));
            altitude = Double.valueOf(intent.getStringExtra("altitude"));
            tv_latitude.setText(latitude+"");
            tv_longitude.setText(longitude+"");
            tv_altitude.setText(altitude+"");

        }
    };
    //SETTING LOCATION ENDS


    @Override
        public void onClick(View view) {
            //if logout is pressed
           if(view == btn_stop){
                //logging out the user
               // firebaseAuth.signOut();
                //closing activity
                //finish();
                //starting login activity
                //startActivity(new Intent(this, LoginActivity.class));
               stopService(new Intent(getBaseContext(),LocationService.class));
               tv_latitude.setText("");
               tv_longitude.setText("");
               tv_altitude.setText("");
            }
        }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(LocationService.str_receiver));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
