package Manager;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.example.boostup.R;
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

import adapters.AdapterUsers;
import models.ModelUser;


public class ManagerUsersFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayout nouserid;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog pd;

    public ManagerUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manager_users, container, false);
        recyclerView = view.findViewById(R.id.users_recycleview);
        nouserid=view.findViewById(R.id.nouserid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //Initialise userlist
        userList = new ArrayList<>();
        //get AllUsers
        getAllUsers();

        return view;
    }/*
    private void getAllUsers() {
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("user").child("Influencers");
        //get all data from path
        Query query=databaseReference.orderByChild("Email");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    ModelUser modelUser=ds.getValue(ModelUser.class);
                    userList.add(modelUser);

                }
                Collections.reverse(userList);
                //adapter
                adapterUsers = new AdapterUsers(getActivity(),userList);
                //set adapter to recylce view
                recyclerView.setAdapter(adapterUsers);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
    private void getAllUsers() {
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        final FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference= firebaseDatabase.getReference("user");
        DatabaseReference db1= databaseReference.child("Influencers");
        Query query=db1.orderByChild("YoutubeViews");
        ValueEventListener eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    String Image = ds.child("Image").getValue(String.class);
                    String Account = ds.child("Account").getValue(String.class);
                    String UserName = ds.child("UserName").getValue(String.class);
                    String YoutubeViews = ds.child("YoutubeViews").getValue(String.class);
                    String TicktokViews = ds.child("TicktokViews").getValue(String.class);
                    String InstaViews =  ds.child("InstaViews").getValue(String.class);

                    String uid=ds.child("uid").getValue(String.class);

                    ModelUser modelUser = new ModelUser(Image, UserName, YoutubeViews, TicktokViews, InstaViews,uid,Account);
                    if(UserName==null || UserName.equals("user") || Account.equals("Delete")){

                    }
                    else{
                        nouserid.setVisibility(View.GONE);
                        userList.add(modelUser);
                        Collections.reverse(userList);
                        //adapter
                        adapterUsers = new AdapterUsers(getActivity(), userList);
                        //set adapter to recylce view
                        recyclerView.setAdapter(adapterUsers);}
                }
                if(userList.size()==0){
                    nouserid.setVisibility(View.VISIBLE);
                }
                else{
                    nouserid.setVisibility(View.GONE);
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
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("user").child("Influencers");
        Query query=databaseReference.orderByChild("Email");
        ValueEventListener eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    String Image = ds.child("Image").getValue(String.class);
                    String UserName = ds.child("UserName").getValue(String.class);
                    String YoutubeViews = ds.child("YoutubeViews").getValue(String.class);
                    String TicktokViews = ds.child("TicktokViews").getValue(String.class);
                    String InstaViews =  ds.child("InstaViews").getValue(String.class);
                    String Account = ds.child("Account").getValue(String.class);
                    String uid=ds.child("uid").getValue(String.class);

                    ModelUser modelUser = new ModelUser(Image, UserName, YoutubeViews, TicktokViews, InstaViews,uid,Account);
                    if(modelUser.getUserName().toLowerCase().contains(s.toLowerCase()) && !Account.equals("Delete")
                            && modelUser.getUserName()!=null && !modelUser.getUserName().equals("user")){
                        nouserid.setVisibility(View.GONE);
                        userList.add(modelUser);
                    }
                    else {
                        nouserid.setVisibility(View.VISIBLE);
                    }

                    adapterUsers = new AdapterUsers(getActivity(),userList);
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
        //get all data from path
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    String Image = ds.child("Image").getValue(String.class);
                    String UserName = ds.child("UserName").getValue(String.class);
                    String YoutubeViews = ds.child("YoutubeViews").getValue(String.class);
                    String TicktokViews = ds.child("TicktokViews").getValue(String.class);
                    String InstaViews =  ds.child("InstaViews").getValue(String.class);
                    String Account = ds.child("Account").getValue(String.class);
                    String uid=ds.child("uid").getValue(String.class);

                    ModelUser modelUser = new ModelUser(Image, UserName, YoutubeViews, TicktokViews, InstaViews,uid,Account);
                    if(modelUser.getUserName().toLowerCase().contains(s.toLowerCase()) && !Account.equals("Delete")
                            && modelUser.getUserName()!=null && !modelUser.getUserName().equals("user")){
                        nouserid.setVisibility(View.GONE);
                        userList.add(modelUser);
                    }
                    else {
                        nouserid.setVisibility(View.VISIBLE);
                    }
                    adapterUsers = new AdapterUsers(getActivity(),userList);
                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();

                    //set adapter to recylce view
                    recyclerView.setAdapter(adapterUsers);
                }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.manager_logout,menu);
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.out_menu){
            pd=new ProgressDialog(getActivity());
            pd.setMessage("Logging Out");
            pd.show();
            signOut();
            Intent i = new Intent(getActivity(),ManagerLogin.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pd.dismiss();
            startActivity(i);
        }
        if(id == R.id.settings){
            Intent i = new Intent(getActivity(),ManagerSettings.class);
            startActivity(i);
        }
        if (id == R.id.youtubesort) {
                Sort("YoutubeViews");
                //YoutubeSort("YoutubeViews");
                Toast.makeText(getActivity(), "Sorted", Toast.LENGTH_SHORT).show();

            }
            else if (id == R.id.instasort) {
                Sort("InstaViews");
                //InstagramSort("InstaViews");
                Toast.makeText(getActivity(), "Sorted", Toast.LENGTH_SHORT).show();

            }
            else if(id == R.id.ticktoksort) {
                Sort("TicktokViews");
                //TicktokSort("TicktokViews");
                Toast.makeText(getActivity(), "Sorted", Toast.LENGTH_SHORT).show();

            }

        return super.onOptionsItemSelected(item);
    }

    private void Sort(String views) {
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        final FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference= firebaseDatabase.getReference("user");
        DatabaseReference db1= databaseReference.child("Influencers");
        Query query=db1.orderByChild(views);
        ValueEventListener eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    String Image = ds.child("Image").getValue(String.class);
                    String UserName = ds.child("UserName").getValue(String.class);
                    String YoutubeViews = ds.child("YoutubeViews").getValue(String.class);
                    String TicktokViews = ds.child("TicktokViews").getValue(String.class);
                    String InstaViews =  ds.child("InstaViews").getValue(String.class);
                    String Account = ds.child("Account").getValue(String.class);
                    String uid=ds.child("uid").getValue(String.class);

                    ModelUser modelUser = new ModelUser(Image, UserName, YoutubeViews, TicktokViews, InstaViews,uid,Account);
                    if(UserName==null || UserName.equals("user") || Account.equals("Delete")){

                    }
                    else{
                        userList.add(modelUser);
                        Collections.reverse(userList);
                        //adapter
                        adapterUsers = new AdapterUsers(getActivity(), userList);
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

}
