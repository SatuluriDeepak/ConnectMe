package com.example.boostup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class manager_posts_fragment extends Fragment {
    FloatingActionButton floatingActionButton;
    FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,db1;
    String uid;

    public manager_posts_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_manager_posts_fragment, container, false);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        uid=user.getUid();
        databaseReference=firebaseDatabase.getReference("user").child("Managers").child(uid);

        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
        return view;
    }

    private void addProduct() {
        Random rand = new Random();
        int rand_int1 = rand.nextInt(1000);
        db1=databaseReference.child("posts").child(String.valueOf(rand_int1));
        final HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("Tittle","One");
        hashMap.put("Description","DP");

        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    db1.updateChildren(hashMap);
                    Toast.makeText(getContext(), "Created", Toast.LENGTH_SHORT).show();

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}