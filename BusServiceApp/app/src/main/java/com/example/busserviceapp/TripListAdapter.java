package com.example.busserviceapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder>{

    private static final String TAG = "RV_AdapterVertical";
    private List<TripListModel> triplist =new ArrayList<TripListModel>();
    private Context context;

    public TripListAdapter(List<TripListModel> triplist, Context context) {
        this.triplist = triplist;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.triplist_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.startAddress.setText(holder.startAddress.getText()+triplist.get(position).getStartAdd());
        holder.endAddress.setText(holder.endAddress.getText()+triplist.get(position).getEndAdd());
        holder.time_date.setText(triplist.get(position).getDate_time());
        holder.busName.setText(triplist.get(position).getBusname());
        holder.driverName.setText(triplist.get(position).getDrivername());
        holder.rating.setText(holder.rating.getText()+triplist.get(position).getRating());
        Uri uri=Uri.parse(triplist.get(position).getDriverPictureUrl());
        Picasso.with(context).load(uri).fit().centerCrop().into(holder.driverPhoto);
    }

    @Override
    public int getItemCount() {
        return triplist.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView startAddress,endAddress,time_date,busName,driverName,rating;
        ImageView driverPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            startAddress=itemView.findViewById(R.id.startAddress_triplist);
            endAddress=itemView.findViewById(R.id.endAddress_triplist);
            time_date=itemView.findViewById(R.id.time_date_triplist);
            busName=itemView.findViewById(R.id.bus_name_triplist);
            driverName=itemView.findViewById(R.id.drivername_triplist);
            rating=itemView.findViewById(R.id.driverrating__triplist);
            driverPhoto=itemView.findViewById(R.id.driverpic_triplist);
        }
    }
}
