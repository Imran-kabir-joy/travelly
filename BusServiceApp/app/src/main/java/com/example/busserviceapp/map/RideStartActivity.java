package com.example.busserviceapp.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.example.busserviceapp.APIClient.APIClient;
import com.example.busserviceapp.APIClient.DirectionApiInterface;
import com.example.busserviceapp.APIClient.DistanceApiInterface;
import com.example.busserviceapp.Aboutus;
import com.example.busserviceapp.DirectionApiModel.DirectionResponse;
import com.example.busserviceapp.DirectionApiModel.OverviewPolyline;
import com.example.busserviceapp.DistanceMatrixApiModel.DistanceResponse;
import com.example.busserviceapp.DistanceMatrixApiModel.Element;
import com.example.busserviceapp.R;
import com.example.busserviceapp.TripsAvtivity;
import com.example.busserviceapp.UserProfile;
import com.example.busserviceapp.modelclass.DriverInformation;
import com.example.busserviceapp.modelclass.Parcel;
import com.example.busserviceapp.modelclass.UserInformation;
import com.example.busserviceapp.registration.loginActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;
import com.google.android.gms.maps.model.Marker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RideStartActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        InfoWindowManager.WindowShowListener{

    private static final String TAG = "RideStartActivity";
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private static final int REQUEST_CALL = 1;

    private Button btn_toggle,btn_check;
    private TextView nav_userName;
    private ImageView nav_userPhoto,move_cam;

    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    InfoWindowManager windowManager;
    MapInfoWindowFragment mapFragment;

    private static final float DEFAULT_ZOOM = 17f;

    Marker destinationMarker,startMarker,busMarker;
    String buskey="abc";

    private BottomSheetBehavior bottomSheetBehavior;
    View reportSheet;ImageView fourdot,driverImage;
    TextView driverName,busNAme,driverLic,vehicleLic,driverExp,vehicleExp,driverphnoe,driverrating;
    ImageView emergencycall;
    Parcel parcel;

    Retrofit retrofitInstance1 = APIClient.instance();
    final DirectionApiInterface directionApiInterface= retrofitInstance1.create(DirectionApiInterface.class);
    OverviewPolyline overviewPolyline;
    Polyline polyline;List<Polyline> lineList=new ArrayList<>();


    DatabaseReference ref_onMapReady;
    ValueEventListener lisener_onMapReady;

    Retrofit retrofitInstance2= APIClient.instance();
    final DistanceApiInterface distanceApiInterface=retrofitInstance2.create(DistanceApiInterface.class);
    int distanceMeasure=0;
    boolean callForDialog=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: calledR");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_start);

        Intent intent=getIntent();
        parcel=intent.getParcelableExtra("parcel");

        drawerLayout=findViewById(R.id.drawer_layout_ridestart_activity_xml);
        navigationView = findViewById(R.id.nav_view_ridestart_activity);
        btn_toggle=findViewById(R.id.toggle_drawer_rideStratActivity);
        toolbar = findViewById(R.id.toolbar_xml);
        btn_check=findViewById(R.id.btn_check);btn_check.setVisibility(View.INVISIBLE);
        move_cam=findViewById(R.id.move_camera_rideStartActivity);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();

        mapFragment = (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.map_ridestart_activity);
        mapFragment.getMapAsync(this);

        windowManager=mapFragment.infoWindowManager();
        windowManager.setHideOnFling(true);


        move_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveCamera(busMarker.getPosition());
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.logout_menu_xml:
                       /* firebaseAuth.signOut();
                        Intent intent=new Intent(RideStartActivity.this, loginActivity.class);

                        ref_onMapReady.removeEventListener(lisener_onMapReady);
                        stopLocationUpdates();
                        mGoogleApiClient.disconnect();
                        startActivity(intent);
                        finish();
                        Toast.makeText(RideStartActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();*/
                        Toast.makeText(RideStartActivity.this, "Can't logout while taking ride.", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.my_account_menu_xml:
                        Intent intent1=new Intent(RideStartActivity.this, UserProfile.class);
                        startActivity(intent1);
                        break;
                    case R.id.journey_info_menu_xml:
                        Intent intent2=new Intent(RideStartActivity.this, TripsAvtivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.about_us_menu_xml:
                        Intent intent3=new Intent(RideStartActivity.this, Aboutus.class);
                        startActivity(intent3);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        setSupportActionBar(toolbar);

        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buildGoogleApiClient();

        reportSheet=findViewById(R.id.bottom_sheet);
        bottomSheetBehavior=BottomSheetBehavior.from(reportSheet);
        fourdot =reportSheet.findViewById(R.id.four_dot_bottomsheet);
        updateHeaderInformation(parcel.getKey());
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i){
                    case 1:
                        fourdot.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                    case 2:
                        fourdot.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                    case 3:
                        fourdot.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                    case 4:
                        fourdot.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float slideOffset) {

            }
        });

        btn_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        final Intent  intent_temp=new Intent(this,RatingActivity.class);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent_temp.putExtra("parcel_temp",new Parcel(parcel.getStart(),parcel.getDestination(),parcel.getBusLatlng(),parcel.getKey(),parcel.getBusName(),parcel.getPlaceName(),parcel.getUsername()));
                startActivity(intent_temp);
            }
        });

    }//End of onCreate

    protected  synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Called");
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        mMap.setMyLocationEnabled(false);
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        startMarker=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
                .title("Start Location")
                .position(parcel.getStart()) );

        destinationMarker=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end))
                .title("Destination Location")
                .position(parcel.getDestination()));

        //Puting bus marker
        busMarker=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker))
                .title(parcel.getBusName())
                .position(parcel.getBusLatlng()));

        getDirectionInformation(startMarker.getPosition(),destinationMarker.getPosition());

        moveCamera(startMarker.getPosition());

        ref_onMapReady=FirebaseDatabase.getInstance().getReference("availableDriver").child(parcel.getKey()).child("l");
        lisener_onMapReady=ref_onMapReady.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: putBusMarker called");
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng buslatlng = new LatLng(locationLat,locationLng);
                    if(busMarker!=null){
                        busMarker.setPosition(buslatlng);
                    }

                        /*markerList.get(busList.getKey()).setPosition(buslatlng);

                        getDistanceInformation(mLastLocation,buslatlng,reachingTime);
                        Log.d(TAG, "onDataChange: distance = "+distanceMeasure);
                        if(distanceMeasure!=0 && distanceMeasure<=500 && !callForDialog){
                            showUpdateDialog(key);   //showing dialog
                        }*/

                    Log.d(TAG, "onDataChange: destinationMarker.getPosition()="+destinationMarker.getPosition());
                    Log.d(TAG, "onDataChange: buslatlng="+buslatlng);

                    try{
                        getDistanceInformation(destinationMarker.getPosition(),buslatlng);
                        Log.d(TAG, "onDataChange: distance = "+distanceMeasure);
                        if(distanceMeasure!=0 && distanceMeasure<=80 && !callForDialog ){
                            showUpdateDialog();
                        }
                    }catch(Exception e){
                        System.out.println("exception="+e);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }//End Of onMapReady


    private  void showUpdateDialog() {
        callForDialog =true;
        distanceMeasure=0;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_finish_ride, null);

        dialogBuilder.setView(view);
        dialogBuilder.setTitle("Your Destination Location is nearby");

        Button btn_ok=view.findViewById(R.id.ok_btn_dialog);btn_ok.setText("Finish Ride");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ref_onMapReady.removeEventListener(lisener_onMapReady);
               callForDialog=false;
               stopLocationUpdates();
               Intent  intent_temp=new Intent(RideStartActivity.this, RatingActivity.class);
               intent_temp.putExtra("parcel_temp",new Parcel(parcel.getStart(),parcel.getDestination(),parcel.getBusLatlng(),parcel.getKey(),parcel.getBusName(),parcel.getPlaceName(),parcel.getUsername()));
               alertDialog.dismiss();
               finish();
               startActivity(intent_temp);

            }
        });

/*        btn_missed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reachingTimeLayout.setVisibility(View.INVISIBLE);
                availablebus_portion.setVisibility(VISIBLE);
                btn1.setVisibility(VISIBLE);
                callForDialog =false;
                distanceMeasure=0;
                callForNearbyBus=true;


                alertDialog.dismiss();
            }
        });*/


    }

    public void getDistanceInformation(LatLng x, LatLng selectedMarkerLatLng/*, final TextView textview*/){

        Log.d(TAG, "getDistanceInformation: called from RideStartActivity");
        String lat2= String.valueOf(x.latitude);
        String long2= String.valueOf(x.longitude);

        String lat1= String.valueOf(selectedMarkerLatLng.latitude);
        String long1= String.valueOf(selectedMarkerLatLng.longitude);

        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("units", "imperial");
        mapQuery.put("origins", lat1+","+long1);
        mapQuery.put("destinations", lat2+","+long2);
        mapQuery.put("key","AIzaSyBXzTJ4tsJOnGHcrDCR4GszzeI5spUp8mg");

        Call<DistanceResponse> call=distanceApiInterface.getDistanceInfo(mapQuery);
        call.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {
                DistanceResponse distanceResponse=response.body();
                Element element=distanceResponse.getRows().get(0).getElements().get(0);
                Log.d(TAG, "onResponse: Distance Respomse=>"+element.getDistance().getValue()+" "+element.getDuration().getText());
                /*textview.setText(element.getDuration().getText());*/
                distanceMeasure=element.getDistance().getValue();
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {

            }
        });
    }


    public void getDirectionInformation(LatLng latLng1,LatLng latLng2){

        String lat2= String.valueOf(latLng2.latitude);
        String long2= String.valueOf(latLng2.longitude);

        String lat1= String.valueOf(latLng1.latitude);
        String long1= String.valueOf(latLng1.longitude);

        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("key","AIzaSyBXzTJ4tsJOnGHcrDCR4GszzeI5spUp8mg");
        mapQuery.put("origin", lat1+","+long1);
        mapQuery.put("destination", lat2+","+long2);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(23.7512191,90.3783289)).title("Start"));
        // mMap.addMarker(new MarkerOptions().position(new LatLng(23.7560154,90.3741217)).title("End"));


        Call<DirectionResponse> call=directionApiInterface.getDirectionInfo(mapQuery);
        call.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
               // Toast.makeText(RideStartActivity.this, "Response body"+response, Toast.LENGTH_SHORT).show();


                DirectionResponse directionResponse=response.body();

                overviewPolyline=directionResponse.getRoutes().get(0).getOverviewPolyline();
                if(overviewPolyline!= null){

                    List<LatLng> latLngList= PolyUtil.decode(overviewPolyline.getPoints());
                    Log.d(TAG, "onResponse: latlngList Size=>"+latLngList.size());

                    if(!lineList.isEmpty()){
                        lineList.clear();
                    }
                    for(int k=0;k<latLngList.size()-1;k++){
                        LatLng origin=latLngList.get(k);
                        LatLng destination=latLngList.get(k+1);
                        polyline=mMap.addPolyline(new PolylineOptions().color(R.color.ash).geodesic(true).add(
                                new LatLng(origin.latitude,origin.longitude),
                                new LatLng(destination.latitude,destination.longitude)).width(12));
                        lineList.add(polyline);
                    }

                } else {
                    Log.d(TAG, "onResponse:overviewPolyline = null ");
                }
            }
            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {

            }
        });
    }

    public void moveCamera(LatLng latLng ){
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: updating location...");

        mLastLocation=location;
        final LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);//can be changed based on need.
        //permission check missing 9:28
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private  void updateHeaderInformation(String key){
        String uid=currentUser.getUid();
        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference("userlist").child(uid);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    UserInformation uf=dataSnapshot.getValue(UserInformation.class);
                    updateUI(uf);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mRef2= FirebaseDatabase.getInstance().getReference("driverlist").child(key);
        mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    DriverInformation df=dataSnapshot.getValue(DriverInformation.class);
                    updateBottomSheet(df);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void makePhoneCall() {
        String number = "999";
        if (number.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(RideStartActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(RideStartActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Log.d(TAG, "makePhoneCall: "+"Enter Phone Number");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private  void updateBottomSheet(DriverInformation df){
        driverImage=reportSheet.findViewById(R.id.driver_image_bottomSheet);
        driverName=reportSheet.findViewById(R.id.driver_name_bottomSheet);
        busNAme=reportSheet.findViewById(R.id.bus_name_bottomSheet);
        driverLic=reportSheet.findViewById(R.id.driving_license_bottomsheet);
        vehicleLic=reportSheet.findViewById(R.id.vehicle_license_bottomsheet);
        driverExp=reportSheet.findViewById(R.id.driving_license_exp_bottomsheet);
        vehicleExp=reportSheet.findViewById(R.id.vehicle_license_exp_bottomsheet);
        driverphnoe=reportSheet.findViewById(R.id.driver_phn_bottomsheet);
        driverrating=reportSheet.findViewById(R.id.driverRating_RideStartActivity);
        emergencycall=reportSheet.findViewById(R.id.emergency_call_xml);

        emergencycall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*makePhoneCall();*/
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:999"));
                startActivity(intent);
                //Toast.makeText(RideStartActivity.this, "Calling 999", Toast.LENGTH_SHORT).show();
            }
        });

        Uri uri=Uri.parse(df.getDriverPictureUri());
        Picasso.with(RideStartActivity.this).load(uri).fit().centerCrop().into(driverImage);
        driverName.setText(df.getDriverName());
        busNAme.setText(df.getBusCatagory());
        driverLic.setText(df.getDriverLicenseNumber());
        driverExp.setText(df.getDriverLicenseExpireDate());
        vehicleLic.setText(df.getVehicleLicenseNumber());
        vehicleExp.setText(df.getVehicleLicenseExpiryDate());
        driverphnoe.setText(df.getDriverPhoneNumber());

        DatabaseReference rating_ref=FirebaseDatabase.getInstance().getReference().child("driverrating").child(df.getUid());
        rating_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalRating=0;Double ratingSum=0.0;
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ratingSum= ratingSum+Double.valueOf(String.valueOf(ds.getValue()));
                    totalRating++;
                    Log.d(TAG, "onDataChange: rating:"+Double.valueOf(String.valueOf(ds.getValue())));
                }
                Log.d(TAG, "onDataChange: total number of rating="+totalRating+" rating sum="+ratingSum);

                driverrating.setText(driverrating.getText()+String.valueOf( df2.format(ratingSum/totalRating) ) );

                Log.d(TAG, "rating:"+driverrating.getText());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private  void updateUI(UserInformation uf){
        Log.d(TAG,"=>"+uf.getUserName()+"\n=>"+uf.getUserProfiePictureUri());
        NavigationView nv=findViewById(R.id.nav_view_ridestart_activity);
        View header=nv.getHeaderView(0);
        nav_userName = header.findViewById(R.id.nav_userName_xml);
        nav_userPhoto=header.findViewById(R.id.nav_userPic_xml);

        nav_userName.setText(uf.getUserName());
        Uri uri=Uri.parse(uf.getUserProfiePictureUri());
        Picasso.with(RideStartActivity.this).load(uri).fit().centerCrop().into(nav_userPhoto);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onWindowShowStarted(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowShown(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowHideStarted(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowHidden(@NonNull InfoWindow infoWindow) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: calledR RideStartActivity");
    }
    
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: calledR RideStartActivity");
        super.onResume();

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: calledR RideStartActivity");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: calledR RideStartActivity");
        super.onRestart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: calledR RideStartActivity");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: calledR RideStartActivity");
        super.onDestroy();
    }

    protected void stopLocationUpdates() {
        Log.d(TAG, "Location update stoping...");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_cancelride, null);

        dialogBuilder.setView(view);
        dialogBuilder.setTitle("Cancel Ride or Exit App ?");

        Button btn_cancel=view.findViewById(R.id.btn_cancel_ride_xml);
        Button btn_exit=view.findViewById(R.id.btn_closeapp_xml);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onBackPressed:btn_cancel:onClick");
                Intent intent=new Intent(RideStartActivity.this,HomeActivity.class);

                stopLocationUpdates();
                mGoogleApiClient.disconnect();
                alertDialog.dismiss();
                ref_onMapReady.removeEventListener(lisener_onMapReady);
                finish();
                startActivity(intent);
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onBackPressed:btn_exit:onClick");
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                ref_onMapReady.removeEventListener(lisener_onMapReady);
                stopLocationUpdates();
                mGoogleApiClient.disconnect();
                alertDialog.dismiss();
                finish();

                startActivity(a);
            }
        });

    }
}//End of Code
