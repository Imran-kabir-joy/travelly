package com.example.busserviceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.busserviceapp.map.HomeActivity;
import com.example.busserviceapp.modelclass.TripHistoryModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import com.example.busserviceapp.modelclass.TripHistoryModel;

public class TripsAvtivity extends AppCompatActivity {
    private static final String TAG = "TripsAvtivity";

    ImageView back_arrow;
    RecyclerView recyclerView;
    DatabaseReference tripdata_ref= FirebaseDatabase.getInstance().getReference().child("triphistory").child(HomeActivity.currentUser.getUid());
    List<TripListModel> mtriplist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: calledT TripsAvtivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_avtivity);
        recyclerView=findViewById(R.id.recyclerview_triplist);
        back_arrow=findViewById(R.id.back_arrow);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //List<TripListModel> mlist=new ArrayList<>();

        tripdata_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mtriplist.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Log.d(TAG, "TripsAvtivity:onDataChange: data paisi");
                    TripHistoryModel tripHistoryModel=ds.getValue(TripHistoryModel.class);
                    mtriplist.add(new TripListModel(tripHistoryModel.getStartAddress()
                            ,tripHistoryModel.getDestinationAddress()
                            ,tripHistoryModel.getRating()
                            ,tripHistoryModel.getDate_time()
                            ,tripHistoryModel.getBusName()
                            ,tripHistoryModel.getDriverName()
                            ,tripHistoryModel.getDriverPhotoUrl()));
                }

                TripListAdapter adapter=new TripListAdapter(mtriplist,getApplicationContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: calledT TripsAvtivity");
        super.onStart();
    }
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: calledT TripsAvtivity");
        super.onResume();

    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: calledT TripsAvtivity");
        super.onRestart();

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: calledT TripsAvtivity");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: calledT TripsAvtivity");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: calledT TripsAvtivity");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
