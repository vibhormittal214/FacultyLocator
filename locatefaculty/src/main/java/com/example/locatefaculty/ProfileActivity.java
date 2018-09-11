package com.example.locatefaculty;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by vibhor on 24-09-2017.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback {

    //firebase auth object

    private FirebaseAuth firebaseAuth;
    LocationManager locationManager;
    LocationManager mlocmag;
    android.location.LocationListener mlocList;
    Location location;
    //view objects
    private TextView textViewUsername,textViewroom,textviewfar;
    private TextView textViewUserid;
    public EditText facultyid;
    public TextView lati;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    SupportMapFragment mapFrag;
    Button btn_start;
    String name,id,lat,id1,name1,longi,alti;
    public DatabaseReference current_user_db,current_user_db1,current_user_db2;
    String user_id;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    LatLng latLng;
    Double z,z1,z2,z3;
    Marker marker;
    SupportMapFragment mapFragment;
    double latt,longg,altii,mylat,mylong;
    private Drawer result;
    private Toolbar toolbar;
    private Typeface montserrat_regular;
    private String[] SOURCE_ARRAY = {"Chat Room","Logout","Sell a book","Purchase a book"};
    private TextView mTitle;
    private AccountHeader accountHeader;
    private String SOURCE;
    private static final int pconstant=1;

    boolean GpsStatus ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile1);
      //  AssetManager assetManager = this.getApplicationContext().getAssets();
       // montserrat_regular = Typeface.createFromAsset(assetManager, "fonts/Montserrat-Regular.ttf");

        createToolbar();
        createDrawer(savedInstanceState, toolbar, montserrat_regular);
        mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //initializing firebase authentication object
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    ACCESS_FINE_LOCATION)) {

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{ACCESS_FINE_LOCATION},
                        pconstant);
            }
        }
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //if the user is not logged in
        //that means current user will return null
        current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child("teacher");
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
        //getting current user
        user_id = firebaseAuth.getCurrentUser().getUid();
        current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child("student");
        //initializing views
        //textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        //textViewUserid = (TextView) findViewById(R.id.textViewUserid);
        textviewfar=(TextView)findViewById(R.id.far);
        facultyid=(EditText)findViewById(R.id.fidd);
        //lati=(TextView)findViewById(R.id.textViewlatitude);
        textViewroom=(TextView)findViewById(R.id.textViewroom);
        btn_start = (Button) findViewById(R.id.btn_start);
     btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id1 = facultyid.getText().toString();
                if (TextUtils.isEmpty(id1)) {
                    Toast.makeText(ProfileActivity.this, "faculty id cant be blank", Toast.LENGTH_LONG).show();
                    return;
                }
                if (ContextCompat.checkSelfPermission(ProfileActivity.this,android. Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            ProfileActivity.this,
                            new String[]{android. Manifest.permission.ACCESS_FINE_LOCATION},
                            1);

                }
                else{
                if(Function.isNetworkAvailable(getApplicationContext()))
                {
                    CheckGpsStatus() ;

                    if(GpsStatus == false)
                    {
                        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), LocationService.class);
                        startService(intent);
                        Toast.makeText(ProfileActivity.this,mylat+"",Toast.LENGTH_LONG).show();
                    current_user_db1 = FirebaseDatabase.getInstance().getReference().child("users").child("teacherdata");
                    current_user_db1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(id1)) {
                                current_user_db2 = FirebaseDatabase.getInstance().getReference().child("users").child("teacherdata").child(id1);
                                current_user_db2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        lat = String.valueOf(dataSnapshot.child("latitude").getValue());
                                        name1 = String.valueOf(dataSnapshot.child("name11").getValue());
                                        longi = String.valueOf(dataSnapshot.child("longitude").getValue());
                                        alti = String.valueOf(dataSnapshot.child("altitude").getValue());
                                        // lati.setText("Faculty  " + name1 + " is at latitude " + lat + " and longitude " + longi);
                                        // Toast.makeText(ProfileActivity.this,name,Toast.LENGTH_LONG).show();
                                        if(!lat.equals(" ")&&!longi.equals(" "))
                                        {
                                            latt = Double.parseDouble(lat);
                                            longg = Double.parseDouble(longi);
                                            altii=Double.parseDouble(alti);
                                            //distance part is added here
                                            Double dist1,dist2,dist3,dist4,dist5,dist6;
                                            Double academic_latitude=28.630454;
                                            Double academic_long=77.372093;
                                            Double al=28.630391;
                                            Double all=77.372097;
                                            Double a2=28.630803;
                                            Double a22=77.372086;
                                            Double caf_lat=28.630456;
                                            Double caf_long=77.370961;
                                            Double oat_lat=28.630475;
                                            Double oat_long=77.371587;
                                            Double G9_Mean=28.63019996;
                                            Double Sd=0.000009;
                                            Double Room_Mean=28.671176354839;
                                            Double G5_Mean=28.6304976571;
                                            Double G1_Mean=28.6306961765;
                                            Double Room_sd=0.00009;
                                            Double Room_2=28.671128;
                                            dist1=distance(latt,longg,academic_latitude,academic_long);
                                            dist2=distance(latt,longg,caf_lat,caf_long);
                                            dist3=distance(latt,longg,oat_lat,oat_long);
                                            dist4=distance(mylat,mylong,latt,longg);
                                            dist5=distance(latt,longg,al,all);
                                            dist6=distance(latt,longg,a2,a22);
                                       /* if(dist1<=0.030)
                                        {
                                            //textViewroom.setText("approximate building is academic block  "+dist1);
                                            z=(G9_Mean-altii)/Sd;
                                            z1=(G5_Mean-altii)/Sd;
                                            z2=(G1_Mean-altii)/Sd;
                                            if(z<0){
                                                z=-1*z;
                                            }
                                            if(z1<0){
                                                z1=-1*z1;
                                            }
                                            if(z2<0){
                                                z2=-1*z2;
                                            }*/
                                            if(dist5<0.019){
                                                textViewroom.setText("G9-G6 OR LT3 PART");
                                            } else if(dist6<0.019){

                                                textViewroom.setText("G5-G1 or LT1-2 part");
                                            }
                                            //}
                                            else if (dist2<=0.030)
                                            {
                                                textViewroom.setText("approximate building is cafeteria building  ");
                                            }
                                            else if(dist3<=0.02)
                                            {
                                                textViewroom.setText("approximate buildig is OAT");
                                            }
                                            else
                                            {
                                                // textViewroom.setText("You are at YOUR hOUSE");
                                                z=((altii-Room_Mean)/Room_sd);
                                                if(z<0){
                                                    z=-1*z;
                                                }
                                                //textViewroom.setText(altii+"");
                                                if(z<1.00){
                                                    textViewroom.setText("in room");
                                                    z=0.0;
                                                }

                                            }
                                            textviewfar.setText("  faculty is  "+String.valueOf((dist4/0.62137)*1000)+"  meters away from you");
                                            mapFragment.getMapAsync(ProfileActivity.this);
                                            stopService(new Intent(getBaseContext(),LocationService.class));
                                        }
                                        else
                                        {
                                            lati.setText("location in null");
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(ProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this,"this faculty id doesnt exist",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(ProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });}
                }else{
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }}
            }

        });
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference newRef = current_user_db.child(currentUserID);
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name = String.valueOf(dataSnapshot.child("name11").getValue());
                id = String.valueOf(dataSnapshot.child("eid11").getValue());
                //textViewUsername.setText("Welcome " + name);
                //textViewUserid.setText("Your Id is "+id);
                mTitle = findViewById(R.id.toolbar_title);
                mTitle.setText("Welcome  "+id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,"Cancelled",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void createToolbar() {
        toolbar = findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
    private void createDrawer(Bundle savedInstanceState, final Toolbar toolbar, Typeface montserrat_regular) {
        SectionDrawerItem item1 = new SectionDrawerItem().withIdentifier(1).withName("CHAT SECTION");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Chat Room")
               .withIcon(R.drawable.chat).withTypeface(montserrat_regular);
        SectionDrawerItem item3 = new SectionDrawerItem().withIdentifier(3).withName("SELL BOOK SECTION")
                .withTypeface(montserrat_regular);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("Sell a book")
                .withIcon(R.drawable.purchase).withTypeface(montserrat_regular);
        SectionDrawerItem item5 = new SectionDrawerItem().withIdentifier(5).withName("PURCHASE BOOK SECTION")
                .withTypeface(montserrat_regular);
        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(6).withName("Purchase a book")
               .withIcon(R.drawable.purchase).withTypeface(montserrat_regular);
        SectionDrawerItem item7 = new SectionDrawerItem().withIdentifier(7).withName("LOGOUT SECTION")
                .withTypeface(montserrat_regular);
        PrimaryDrawerItem item8 = new PrimaryDrawerItem().withIdentifier(8).withName("Logout")
                .withIcon(R.drawable.logout).withTypeface(montserrat_regular);
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ic_placeholder)
                .withSavedInstance(savedInstanceState)
                .build();
        result = new DrawerBuilder()
                .withAccountHeader(accountHeader)
                .withActivity(this)
                .withToolbar(toolbar)
                .withSelectedItem(1)
                .addDrawerItems(item1, item2, item3, item4,item5, item6, item7, item8/*, item9,
                        item10, item11, item12, item13 /*item14/*, item15, item16, item17, item18*/)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        int selected = (int) (long) drawerItem.getIdentifier();
                        switch (selected) {
                            case 2:
                                SOURCE = SOURCE_ARRAY[0];
                                if(Function.isNetworkAvailable(getApplicationContext()))
                                {
                                    startActivity(new Intent(ProfileActivity.this, chat.class));

                                }else{
                                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case 8:
                                SOURCE = SOURCE_ARRAY[1];
                                if(Function.isNetworkAvailable(getApplicationContext()))
                                {
                                    firebaseAuth.signOut();
                                    finish();
                                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                }else{
                                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case 4:
                                SOURCE = SOURCE_ARRAY[2];
                                if(Function.isNetworkAvailable(getApplicationContext()))
                                {
                                    startActivity(new Intent(ProfileActivity.this, sell.class));
                                    //onLoadingSwipeRefreshLayout();
                                    // mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                }else{
                                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 6:
                                SOURCE = SOURCE_ARRAY[3];
                                if(Function.isNetworkAvailable(getApplicationContext()))
                                {
                                    startActivity(new Intent(ProfileActivity.this, purchase.class));
                                    //onLoadingSwipeRefreshLayout();
                                    // mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                }else{
                                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }
    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException se) {

        }

        //Edit the following as per you needs
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        //
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng placeLocation = new LatLng(latt,longg); //Make them global
        if (marker != null) {
            marker.remove();
        }
        marker = googleMap.addMarker(new MarkerOptions()
                .position(placeLocation)
                .snippet(
                        "faculty location")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("ME"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18),18,null);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_red1:
                if(Function.isNetworkAvailable(getApplicationContext()))
                {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(this, LoginActivity.class));

                }else{
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.chat:
                if(Function.isNetworkAvailable(getApplicationContext()))
                {
                    startActivity(new Intent(ProfileActivity.this, chat.class));

                }else{
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sell:
                if(Function.isNetworkAvailable(getApplicationContext()))
                {
                    startActivity(new Intent(ProfileActivity.this, sell.class));

                }else{
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.purchase:
                if(Function.isNetworkAvailable(getApplicationContext()))
                {
                    startActivity(new Intent(ProfileActivity.this, purchase.class));

                }else{
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;




        }
        return super.onOptionsItemSelected(item);
    }
    //calculating distance between 2 latitude
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    //calculating distance ended
    public void CheckGpsStatus(){

        locationManager = (LocationManager)ProfileActivity.this.getSystemService(LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            mylat = Double.valueOf(intent.getStringExtra("latutide"));
            mylong = Double.valueOf(intent.getStringExtra("longitude"));

        }
    };
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
    @Override
    public void onClick(View view) {
}}