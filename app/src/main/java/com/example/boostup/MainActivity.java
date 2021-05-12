package com.example.boostup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Manager.ManagerLogin;


public class MainActivity extends AppCompatActivity {
    private  static FirebaseAuth mAuth;
    private  static FirebaseUser user;
    private  static String current;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
    }


    private void managerpage() {
        Intent intent=new Intent(this, ManagerLogin.class);


        startActivity(intent);
    }
    public void influence() {
        Intent intent=new Intent(this,InfluencerLogin.class);
        startActivity(intent);
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected()){
            return  true;

        }
        else{
            return false;
        }

    }

    private  void checkUserstatus() {
        if (user == null) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    influence();
                    finish();
                }
            };
            Handler h = new Handler();
            h.postDelayed(r, 700);
            //influence();
        }
        else {
            current=user.getUid();
            firebaseDatabase=FirebaseDatabase.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child("Influencers");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean bool=false;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if(current.equals(snapshot.getKey())){
                            bool=true;
                        }
                    }
                    if(bool){
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                influence();
                                finish();
                            }
                        };
                        Handler h = new Handler();
                        h.postDelayed(r, 700);
                    }
                    else{
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                // if you are redirecting from a fragment then use getActivity() as the context.
                                // To close the CurrentActitity, r.g. SpalshActivity
                                managerpage();
                                finish();
                            }
                        };
                        Handler h = new Handler();
                        h.postDelayed(r, 700);
                        //managerpage();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        isNetworkAvailable(this);
        if(isNetworkAvailable(this)) {
            checkUserstatus();
        }
        else{
            AlertDialog.Builder myAlert = new AlertDialog.Builder(MainActivity.this);
            myAlert.setTitle("Warning!");
            myAlert.setMessage("No Internet Connection!");
            myAlert.setPositiveButton("OK", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            myAlert.show();
        }
    }

}
