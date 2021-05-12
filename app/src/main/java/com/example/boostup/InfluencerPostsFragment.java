package com.example.boostup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Manager.ManagerLogin;
import Manager.ManagerSettings;
import adapters.AdapterPosts;
import models.ModelPosts;


public class InfluencerPostsFragment extends Fragment {
    FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog pd;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,db1;
    String userid;
    List<ModelPosts> modelPosts;
    RecyclerView recyclerView;
    AdapterPosts adapterPosts;



    public InfluencerPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_influencer_posts, container, false);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        userid=user.getUid();
        databaseReference=firebaseDatabase.getReference("Posts");
        modelPosts = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        getAllPosts();

        return view;
    }
    private void getAllPosts() { ;
        Query query=databaseReference.orderByChild("Tittle");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelPosts.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    String tittle = ds.child("Tittle").getValue(String.class);
                    String description = ds.child("Description").getValue(String.class);
                    String uid = ds.child("uid").getValue(String.class);
                    Log.i("posts",tittle+" "+description+" "+uid);


                        ModelPosts modelPost = new ModelPosts(uid,tittle,description);
                        modelPosts.add(modelPost);
                        Collections.reverse(modelPosts);
                        adapterPosts = new AdapterPosts(getActivity(),modelPosts);
                        recyclerView.setAdapter(adapterPosts);



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);


    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.logout_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.out_menu){
            pd=new ProgressDialog(getActivity());
            pd.setMessage("Logging Out");
            pd.show();
            signOut();
            Intent i = new Intent(getActivity(), ManagerLogin.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pd.dismiss();
            startActivity(i);
        }
        if(id == R.id.settings){
            Intent i = new Intent(getActivity(), ManagerSettings.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    private void signOut() {
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getActivity());
        if(account!=null){
            String PersonName=account.getDisplayName();
            String PersonGivenName=account.getGivenName();
            String PersonFamilyName=account.getFamilyName();
            String PersonEmail=account.getEmail();
            String PersonId=account.getId();
            Uri PersonPhoto=account.getPhotoUrl();
            //Toast.makeText(this, "Welcome "+PersonName, Toast.LENGTH_SHORT).show();
        }
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
    @Override
    public void onStart() {
        if(isNetworkAvailable(getContext())) {
        }
        else{
            androidx.appcompat.app.AlertDialog.Builder myAlert = new androidx.appcompat.app.AlertDialog.Builder(getContext());
            myAlert.setTitle("Warning!");
            myAlert.setMessage("No Internet Connection!");
            myAlert.setPositiveButton("OK", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            myAlert.show();
        }
        super.onStart();
    }
}