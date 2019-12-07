package com.example.busserviceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busserviceapp.map.HomeActivity;
import com.example.busserviceapp.modelclass.UserInformation;
import com.example.busserviceapp.registration.RegistrationActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity {
    private static final String TAG = "UserProfile";

    private ImageView pic,back_arrow;
    private TextView change_pic,usernumber,username,useremail;
    Button btn_update;
    DatabaseReference profile_detailse_ref= FirebaseDatabase.getInstance().getReference().child("userlist");
    DatabaseReference userlist_ref= FirebaseDatabase.getInstance().getReference().child("userlist");
    ValueEventListener lisener_profile_detailse_ref;
    StorageReference storageRef=FirebaseStorage.getInstance().getReference().child("UserProfilePicture");
    static int REQUESTCODE=2;
    static int PIC_IMAGE=1;
    private  Uri pickedPhotoUri;
    private String photoDownloadUriString;
    private UserInformation uf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: calledUP UserProfile");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        pic=findViewById(R.id.pic_userProfile);
        change_pic=findViewById(R.id.change_pic_userProfile);
        username=findViewById(R.id.username_userProfile);
        usernumber=findViewById(R.id.usernumber_userProfile);
        useremail=findViewById(R.id.useremail_userProfile);
        back_arrow=findViewById(R.id.back_arrow_userProfile);
        btn_update=findViewById(R.id.btn_update_userProfile);btn_update.setVisibility(View.INVISIBLE);

        profile_detailse_ref.child(HomeActivity.currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 uf=dataSnapshot.getValue(UserInformation.class);
                username.setText(uf.getUserName());
                useremail.setText((uf.getUserEmail()));
                usernumber.setText(uf.getUserPhoneNumber());

                Uri uri=Uri.parse(uf.getUserProfiePictureUri());
                Picasso.with(UserProfile.this).load(uri).fit().centerCrop().into(pic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //profile_detailse_ref.removeEventListener(lisener_profile_detailse_ref);
                onBackPressed();
            }
        });

        change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_update.setVisibility(View.INVISIBLE);
                change_pic.setText(" Uploading picture... ");
                change_pic.setClickable(false);
                btn_update.setClickable(false);

                final StorageReference imagePath=storageRef.child(pickedPhotoUri.getLastPathSegment());
                imagePath.putFile(pickedPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"Upload Complete");
                        getDownloadUri(imagePath);
                    }
                });
            }
        });

    }//end of onCreate.

    private void openGallery(){
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,PIC_IMAGE);
    }

    private void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(UserProfile.this
                , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){

            if(ActivityCompat.shouldShowRequestPermissionRationale(UserProfile.this
                    ,Manifest.permission.READ_EXTERNAL_STORAGE) ){
                Toast.makeText(this, "Provide Required Permission", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(UserProfile.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUESTCODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PIC_IMAGE && resultCode==RESULT_OK && data!= null){
            pickedPhotoUri=data.getData();
            pic.setImageURI(pickedPhotoUri);
            btn_update.setVisibility(View.VISIBLE);
        }
    }

    private void getDownloadUri(StorageReference mStorage){
        Log.d(TAG,"getDownloadUrl Called");
        mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setDownloadUri(uri);
                uploadUserInformation(userlist_ref);
            }
        });
    }

    private void setDownloadUri(Uri uri){
        Log.d(TAG,"setDownloadUri Called");
        photoDownloadUriString=uri.toString();
        uf.setUserProfiePictureUri(photoDownloadUriString);
    }

    private void uploadUserInformation(DatabaseReference mRef){
        Log.d(TAG,"uploadUserInformation Called");
        Log.d(TAG,"===> "+ uf.getUserProfiePictureUri());
        String uid=uf.getUid();
        mRef.child(uid).setValue(uf);
        change_pic.setText(" Update Photo ");
        btn_update.setVisibility(View.INVISIBLE);

        change_pic.setClickable(true);
        btn_update.setClickable(true);

        Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: calledUP UserProfile");
        requestStoragePermission();
        super.onStart();
    }
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: calledUP UserProfile");
        super.onResume();

    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: calledUP UserProfile");
        super.onRestart();

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: calledUP UserProfile");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: calledUP UserProfile");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: calledUP UserProfile");
        super.onDestroy();
    }
}
