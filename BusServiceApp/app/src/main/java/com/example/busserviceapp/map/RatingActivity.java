package com.example.busserviceapp.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.example.busserviceapp.APIClient.APIClient;
import com.example.busserviceapp.APIClient.DirectionApiInterface;
import com.example.busserviceapp.DirectionApiModel.DirectionResponse;
import com.example.busserviceapp.DirectionApiModel.OverviewPolyline;
import com.example.busserviceapp.R;
import com.example.busserviceapp.modelclass.DriverInformation;
import com.example.busserviceapp.modelclass.Parcel;
import com.example.busserviceapp.modelclass.Report;
import com.example.busserviceapp.modelclass.TripHistoryModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RatingActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        InfoWindowManager.WindowShowListener  {
    int flag=0;


    private static final String TAG = "RatingActivity";
    FirebaseUser currentUser;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    InfoWindowManager windowManager;
    MapInfoWindowFragment mapFragment;

    private static final float DEFAULT_ZOOM = 12f;

    Marker destinationMarker,startMarker;
    Parcel parcel;

    Retrofit retrofitInstance1 = APIClient.instance();
    final DirectionApiInterface directionApiInterface= retrofitInstance1.create(DirectionApiInterface.class);
    OverviewPolyline overviewPolyline;
    Polyline polyline;List<Polyline> lineList=new ArrayList<>();

    RatingBar ratingBar;
    Button btn_submit;
    EditText et_report;
    TextView tv_time_date,tv_drivername,tv_busname;
    ImageView driverPhoto;
    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("driverlist");
    DriverInformation driverInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        ratingBar=findViewById(R.id.ratingbar_ratingActivity);
        btn_submit=findViewById(R.id.btn_submit_ratingActivity);
        et_report=findViewById(R.id.report_ratingActivity);
        tv_time_date=findViewById(R.id.time_date_ratingActivity);
        tv_drivername=findViewById(R.id.drivername_ratingActivity);
        driverPhoto=findViewById(R.id.driverimage_ratingActivity);
        tv_busname=findViewById(R.id.busname_ratingActivity);

        currentUser=FirebaseAuth.getInstance().getCurrentUser();

        tv_time_date.setText(getDateTime());

        Intent intent=getIntent();
        parcel=intent.getParcelableExtra("parcel_temp");

        Log.d(TAG, "onCreate: paisi="+parcel.getStart()+"\n"+parcel.getDestination());

        mapFragment = (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.map_ratingActivity);
        mapFragment.getMapAsync(RatingActivity.this);

        windowManager=mapFragment.infoWindowManager();
        windowManager.setHideOnFling(true);

        buildGoogleApiClient();

        tv_time_date.setText(getDateTime());
        tv_busname.setText("o  "+parcel.getBusName());



        reference1.child(parcel.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: called");
                if(dataSnapshot.exists()){
                    Log.d(TAG, "onDataChange: datasnapshot exits");
                    driverInformation=dataSnapshot.getValue(DriverInformation.class);
                    Log.d(TAG, "driverinfo="+driverInformation.getDriverName());
                    loadInfrmation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //do nothing
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==0){
                    Toast.makeText(RatingActivity.this, "Loading..", Toast.LENGTH_SHORT).show();
                }else {
                    if(ratingBar.getRating()==0.0 ){
                        Toast.makeText(RatingActivity.this, "Please give your driver a rating.", Toast.LENGTH_SHORT).show();
                    }else{
                        TripHistoryModel ratingModel=new TripHistoryModel(
                                getAddress(parcel.getStart().latitude,parcel.getStart().longitude)
                                ,getAddress(parcel.getDestination().latitude,parcel.getDestination().longitude)
                                , getDateTime(0)
                                ,parcel.getBusName()
                                ,ratingBar.getRating()+""
                                ,driverInformation.getDriverPictureUri()
                                ,driverInformation.getDriverName()
                        );
                        DatabaseReference reference0 = FirebaseDatabase.getInstance().getReference().child("triphistory").child(currentUser.getUid());
                        reference0.child(reference0.push().getKey()).setValue(ratingModel);

                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("driverrating").child(parcel.getKey());
                        reference2.child(reference2.push().getKey()).setValue(ratingBar.getRating());

                        if(!et_report.getText().toString().equals("")){
                            DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("reportlist").child(parcel.getKey());
                            reference3.child(reference3.push().getKey()).setValue(new Report(et_report.getText().toString(),getDateTime(1.0),parcel.getUsername()));
                        }
                        showDialog();
                    }
                }


            }
        });

    }//end of onCrete

    public void showDialog(){

        final Intent intent=new Intent(RatingActivity.this, HomeActivity.class);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RatingActivity.this);
        final View view = LayoutInflater.from(RatingActivity.this).inflate(R.layout.dialog_layout_ride_done, null);

        dialogBuilder.setView(view);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        stopLocationUpdates();
        mGoogleApiClient.disconnect();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                alertDialog.dismiss();
                finish();
                startActivity(intent);
            }
        }, 2000);
    }

    public void loadInfrmation(){
        Uri uri=Uri.parse(driverInformation.getDriverPictureUri());
        Picasso.with(RatingActivity.this).load(uri).fit().centerCrop().into(driverPhoto);
        tv_drivername.setText(driverInformation.getDriverName());
        flag++;
    }


    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(RatingActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            //Address add = addresses.get(0);

            String address=(addresses.get(0)).getAddressLine(0);
            String temp="";
            String array[]= address.split(",");
            for(int i=0;i<array.length-1;i++){
                temp=temp+array[i];
            }

            Log.v("IGA", "==>"+"Address: " + temp);
            //return  add.getAddressLine(0);
            return  temp;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return  "N/A";
    }

    public String getDateTime(){
        DateFormat df = new SimpleDateFormat("d MMM yyyy, h:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        return  "o  "+date;
    }

    public String getDateTime(int x){
        DateFormat df = new SimpleDateFormat("d MMM yyyy, h:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        return  date;
    }
    public String getDateTime(double x){
        DateFormat df = new SimpleDateFormat("d MMM yyyy,h:mma");
        String date = df.format(Calendar.getInstance().getTime());
        return  date;
    }



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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.starts))
                .title("Start Location")
                .position(parcel.getStart()) );

        destinationMarker=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ends))
                .title("Destination Location")
                .position(parcel.getDestination()));

        getDirectionInformation(startMarker.getPosition(),destinationMarker.getPosition());

        moveCamera(startMarker.getPosition());
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
                                new LatLng(destination.latitude,destination.longitude)).width(8));
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
    public void moveCamera(Location location){
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),DEFAULT_ZOOM));
    }
    public void moveCamera(LatLng latLng ){
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    protected void stopLocationUpdates() {
        Log.d(TAG, "Location update stoping...");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: updating location...");

        mLastLocation=location;
        final LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


}
