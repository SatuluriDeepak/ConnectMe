package com.example.boostup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Objects;

import adapters.AdapterInfluencer;
import adapters.AdapterUsers;
import models.ModelUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfluencerUsersFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayout nouserid;
    AdapterInfluencer adapterUsers;
    List<ModelUser> userList;
    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog pd;


    public InfluencerUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_influencer_users, container, false);
        nouserid=view.findViewById(R.id.nouserid);
        recyclerView = view.findViewById(R.id.users_recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Initialise userlist
        userList = new ArrayList<>();
        //get AllUsers
        getAllUsers();
        return view;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.out_menu){
            pd=new ProgressDialog(getActivity());
            pd.setMessage("Logging Out");
            pd.show();
            signOut();
            Intent i = new Intent(getActivity(),InfluencerLogin.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pd.dismiss();
            startActivity(i);
        }
        if(id == R.id.settings){
            Intent i = new Intent(getActivity(),InfluencerSettings.class);
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
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);

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
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getActivity()));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.influencer_logout,menu);
        MenuItem item=menu.findItem(R.id.search);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Final search
                //if search is not empty perform action
                if(!TextUtils.isEmpty(s.trim())){
                    searchUser(s);
                }
                else{
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //suggestions

                if(!TextUtils.isEmpty(s.trim())){
                    searchUser(s);

                }
                else{
                    getAllUsers();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    private void getAllUsers() {
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        final FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference= firebaseDatabase.getReference("user");
        DatabaseReference db1= databaseReference.child("Managers");
        Query query=db1.orderByChild("Email");
        ValueEventListener eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    String Image = ds.child("Image").getValue(String.class);
                    String UserName = ds.child("UserName").getValue(String.class);
                    String CompanyName=ds.child("CompanyName").getValue(String.class);
                    String Email=ds.child("Email").getValue(String.class);
                    String Account = ds.child("Account").getValue(String.class);
                    String uid=ds.child("uid").getValue(String.class);

                    ModelUser modelUser = new ModelUser(Image, UserName, CompanyName, Email,uid,Account);
                    if(UserName==null || UserName.equals("Manager")|| modelUser.getAccount().equals("Delete")){
                        nouserid.setVisibility(View.VISIBLE);
                    }
                    else{
                        nouserid.setVisibility(View.GONE);
                        userList.add(modelUser);
                        Collections.reverse(userList);
                        //adapter
                        adapterUsers = new AdapterInfluencer(getActivity(), userList);
                        //set adapter to recylce view
                        recyclerView.setAdapter(adapterUsers);}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(eventListener);

    }
    private void searchUser(final String s) {
        nouserid.setVisibility(View.GONE);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("user").child("Managers");
        //get all data from path
        Query query=databaseReference.orderByChild("Email");
        ValueEventListener eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    String Image = ds.child("Image").getValue(String.class);
                    String UserName = ds.child("UserName").getValue(String.class);
                    String CompanyName=ds.child("CompanyName").getValue(String.class);
                    String Email=ds.child("Email").getValue(String.class);
                    String Account = ds.child("Account").getValue(String.class);
                    String uid=ds.child("uid").getValue(String.class);
                    ModelUser modelUser = new ModelUser(Image, UserName, CompanyName, Email,uid,Account);
                    if(modelUser.getUserName().toLowerCase().contains(s.toLowerCase()) && !modelUser.getAccount().equals("Delete")
                            && modelUser.getUserName()!=null && !modelUser.getUserName().equals("Manager")){
                        nouserid.setVisibility(View.GONE);
                        userList.add(modelUser);
                    }
                    else{
                        nouserid.setVisibility(View.VISIBLE);
                        //display empty;
                    }
                    //adapter
                    adapterUsers = new AdapterInfluencer(getActivity(),userList);
                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();
                    //set adapter to recylce view
                    recyclerView.setAdapter(adapterUsers);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(eventListener);

        /*
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    String Image = ds.child("Image").getValue(String.class);
                    String UserName = ds.child("UserName").getValue(String.class);
                    String CompanyName=ds.child("CompanyName").getValue(String.class);
                    String Email=ds.child("Email").getValue(String.class);
                    String Account = ds.child("Account").getValue(String.class);
                    String uid=ds.child("uid").getValue(String.class);
                    ModelUser modelUser = new ModelUser(Image, UserName, CompanyName, Email,uid,Account);
                    if(modelUser.getUserName().toLowerCase().contains(s.toLowerCase()) && !modelUser.getAccount().equals("Delete")
                            && modelUser.getUserName()!=null && !modelUser.getUserName().equals("Manager")){
                        userList.add(modelUser);
                    }
                    else{
                        nouserid.setVisibility(View.VISIBLE);
                        //display empty;
                    }
                    //adapter
                    adapterUsers = new AdapterInfluencer(getActivity(),userList);
                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();

                    //set adapter to recylce view
                    recyclerView.setAdapter(adapterUsers);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
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
        if(isNetworkAvailable(requireContext())) {
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
