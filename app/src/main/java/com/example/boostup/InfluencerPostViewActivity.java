package com.example.boostup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Manager.ManagerProfileDisplay;

public class InfluencerPostViewActivity extends AppCompatActivity {
    String message,uid,managerId,sendid;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    TextView tittleView,descriptionView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_influencer_post_view);


        Intent intent = getIntent();
        message = intent.getStringExtra("tittle");
        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        uid=user.getUid();
        //Log.i("Tag","Current User "+uid);
        databaseReference= FirebaseDatabase.getInstance().getReference("Posts");

        tittleView = findViewById(R.id.tittle);
        descriptionView = findViewById(R.id.description);

        getPost();
    }
    private void getPost() {
        databaseReference.child(message).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                String tittle = ds.child("Tittle").getValue(String.class);
                String description = ds.child("Description").getValue(String.class);
                managerId = ds.child("uid").getValue(String.class);

                tittleView.setText(tittle);;
                descriptionView.setText(description);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


    public void OpenProfile(View view) {

        Intent i = new Intent(InfluencerPostViewActivity.this,ManagerProfileDisplay.class);
        i.putExtra(sendid,managerId);
        //Toast.makeText(context, "Hello "+userName, Toast.LENGTH_SHORT).show();
        startActivity(i);

    }
}