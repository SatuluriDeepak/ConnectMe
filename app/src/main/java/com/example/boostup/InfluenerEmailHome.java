package com.example.boostup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import Manager.manager_posts;
import Manager.manager_posts_fragment;
import notifications.Token;

public class InfluenerEmailHome extends AppCompatActivity {
    Button logout;
    FirebaseAuth mAuth;

    TextView tv;
    DatabaseReference databaseReference;
    FirebaseFirestore fstore;
    FirebaseUser user;
    ActionBar actionBar;
    private GoogleSignInClient mGoogleSignInClient;
    //private GoogleSignInOptions gso;
    private String USER="user",uid;
    ProgressDialog pd;
    int f=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_influener_email_home);
        pd=new ProgressDialog(this);

        actionBar=getSupportActionBar();
        actionBar.setTitle("Profile");

        BottomNavigationView bottomNavigationView=findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);

        actionBar.setTitle("Home");
        HomeEmailFragment fragment1=new HomeEmailFragment();
        FragmentTransaction ft1= getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();

        mAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("user").child("Influencers");

        //userId=mAuth.getCurrentUser().getUid();
        //tv=findViewById(R.id.person_n);
        user=mAuth.getCurrentUser();
        uid=user.getUid();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        CheckUserStatus();
        updateToken(FirebaseInstanceId.getInstance().getToken());

        /*
        logout=findViewById(R.id.out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                //mAuth.signOut();
                Intent i = new Intent(InfluenerEmailHome.this,InfluencerLogin.class);
                startActivity(i);
            }
        });*/

/*
        Intent intent = getIntent();
        //mail = intent.getStringExtra(ReceivedMessage).toString();
        //Log.i("uid", mail);
        writeDatabase();*/


    }
    
    private void updateToken(String token){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token mtoken=new Token(token);
        ref.child(user.getUid()).setValue(mtoken);
    }
    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
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

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener=new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_products:
                            actionBar.setTitle("Dashboard");
                            InfluencerPostsFragment fragment=new InfluencerPostsFragment();
                            FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content,fragment,"");
                            ft.commit();
                            return true;
                        case R.id.nav_profile:
                            actionBar.setTitle("Profile");
                            ProfileEmailFragment fragment2=new ProfileEmailFragment();
                            FragmentTransaction ft2= getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,fragment2,"");
                            ft2.commit();
                            //Profile Fragment
                            return true;
                        case R.id.nav_chat:
                            actionBar.setTitle("Chats");
                            ChatEmailFragment fragment3=new ChatEmailFragment();
                            FragmentTransaction ft3= getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content,fragment3,"");
                            ft3.commit();
                            //Settings Fragment
                            return true;
                        case R.id.users_display:
                            actionBar.setTitle("Users");
                            InfluencerUsersFragment fragment4=new InfluencerUsersFragment();
                            FragmentTransaction ft4= getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content,fragment4,"");
                            ft4.commit();
                            //Settings Fragment
                            return true;


                    }
                    return false;
                }
            };





    /*
    public void writeDatabase() {

        DocumentReference documentReference= fstore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                tv.setText(documentSnapshot.getString("Email"));

            }
        });

        databaseReference= FirebaseDatabase.getInstance().getReference(USER).child("Details");
        //String keyId=databaseReference.push().getKey();
        //databaseReference=FirebaseDatabase.getInstance().getReference().child("Details");
        databaseReference.child(mail).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //String keyId=databaseReference.push().getKey();
                //String value = dataSnapshot.getValue(String.class);
                String Uname=dataSnapshot.getValue(String.class);
                tv.setText(Uname);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        }
        */


    @Override
    public void onPause() {
        super.onPause();
        String time=String.valueOf(System.currentTimeMillis());
        CheckOnlineStatus(time);
    }

    @Override
    public void onResume() {
        super.onResume();
        CheckOnlineStatus("online");
    }
    private void CheckOnlineStatus(String status) {
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("user").child("Influencers").child(uid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("OnlineStatus",status);
        dbref.updateChildren(hashMap);
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
        if(isNetworkAvailable(this)) {
            CheckUserStatus();
            CheckOnlineStatus("online");
        }
        else{
            androidx.appcompat.app.AlertDialog.Builder myAlert = new androidx.appcompat.app.AlertDialog.Builder(this);
            myAlert.setTitle("Warning!");
            myAlert.setMessage("No Internet Connection!");
            myAlert.setPositiveButton("OK", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            myAlert.show();
        }
        Query query=databaseReference.orderByChild("Email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    String review=""+ds.child("Review").getValue();
                    if(review==null || review==""){
                    }
                    else if(f==1){
                        androidx.appcompat.app.AlertDialog.Builder myAlert = new androidx.appcompat.app.AlertDialog.Builder(InfluenerEmailHome.this);
                        myAlert.setTitle("Message!");
                        myAlert.setMessage("Your last post review is : "+review);
                        myAlert.setPositiveButton("OK", new
                                DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        f=0;
                                        HashMap<String,Object> result= new HashMap<>();
                                        result.put("Review","");
                                        databaseReference.child(user.getUid()).updateChildren(result);
                                    }
                                });
                        myAlert.show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onStart();
    }
    private void CheckUserStatus() {
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("Current_USERID",user.getUid());
            editor.apply();
        }
        else{
            startActivity(new Intent(InfluenerEmailHome.this,InfluencerLogin.class));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String time=String.valueOf(System.currentTimeMillis());
        CheckOnlineStatus(time);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finish();
    }

}
