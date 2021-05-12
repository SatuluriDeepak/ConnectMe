package com.example.boostup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Manager.ManagerChatActivity;
import Manager.Manager;
import adapters.AdapterAddUsers;
import models.ModelChatList;
import models.ModelUser;

public class UserProfile extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    private List<ModelChatList> chatListList;
    private List<ModelUser>userList;
    private AdapterAddUsers adapterChatList;
    Context context;
    String phn,sendid,senduid,message,uid,number,sendemail;
    TextView pins,ptic,pyou,pname,pinsmoney,pyoumoney,ptickmoney,pphone,pemail;
    ImageView puser;
    RatingBar minimumRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        uid=user.getUid();
        pins=findViewById(R.id.pinsviews);
        ptic=findViewById(R.id.ptickviews);
        pyou=findViewById(R.id.pyouviews);
        pname=findViewById(R.id.pname);
        puser=findViewById(R.id.pimage);
        pinsmoney=findViewById(R.id.pinsmoney);
        pyoumoney=findViewById(R.id.pyoumoney);
        ptickmoney=findViewById(R.id.ptickmoney);
        pphone=findViewById(R.id.pphone);
        pemail=findViewById(R.id.pemail);


        Intent intent = getIntent();
        message = intent.getStringExtra(sendid);
        chatListList=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("user").child("Influencers");
        getAllUsers();
        minimumRating = findViewById(R.id.myRatingBar);

    }


    private void getAllUsers() {
       // FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        //Query query=databaseReference.orderByChild("Email").equalTo(user.getEmail());
        databaseReference.child(message).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String dname=""+dataSnapshot.child("UserName").getValue().toString();
                    String you=""+dataSnapshot.child("YoutubeViews").getValue().toString();
                    String ins= ""+dataSnapshot.child("InstaViews").getValue().toString();
                    String tic=""+dataSnapshot.child("TicktokViews").getValue().toString();
                    String image =""+dataSnapshot.child("Image").getValue().toString();
                    String ymoney=""+dataSnapshot.child("YoutubeMoney").getValue().toString();
                    String tmoney=""+dataSnapshot.child("TicktokMoney").getValue().toString();
                    String imoney=""+dataSnapshot.child("InstagramMoney").getValue().toString();
                    String phone=""+dataSnapshot.child("Phone").getValue().toString();
                    number=""+dataSnapshot.child("Phone").getValue().toString().trim();
                    String email=""+dataSnapshot.child("Email").getValue().toString();
                    sendemail=""+dataSnapshot.child("Email").getValue().toString().trim();
                    pins.setText(ins);
                    pyou.setText(you);
                    ptic.setText(tic);
                    pname.setText(dname);
                    pinsmoney.setText(imoney);
                    pyoumoney.setText(ymoney);
                    ptickmoney.setText(tmoney);
                    pphone.setText(phone);
                    pemail.setText(email);

                try{
                    Picasso.get().load(image).into(puser);
                }
                catch (Exception e){
                    // Toast.makeText(getActivity(), "Hello "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Picasso.get().load(R.drawable.userphoto).into(puser);

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void SendMessaage(View view) {
        Intent intent=new Intent(this, ManagerChatActivity.class);
        intent.putExtra(senduid,message);
        startActivity(intent);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usersmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.out_menu){
            Intent i=new Intent(UserProfile.this,MarkedUsersActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        CheckOnlineStatus("online");
    }
    private void CheckOnlineStatus(String status) {
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("user").child("Manager").child(uid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("OnlineStatus",status);
        dbref.updateChildren(hashMap);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CheckOnlineStatus("online");
      
        Intent intent= new Intent(this, Manager.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra(send,message);
        startActivity(intent);
    }

    public void MakeCall(View view) {
        if(number.length()>10){
            Intent intent= new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+number));
            startActivity(intent);
        }

    }

    public void SendMail(View view) {
        Intent sendEmail=new Intent(Intent.ACTION_SEND);
        sendEmail.setData(Uri.parse("mailto:"));
        sendEmail.setType("text/plain");
        sendEmail.putExtra(sendEmail.EXTRA_EMAIL,new String[]{sendemail});
        try{
            startActivity(Intent.createChooser(sendEmail,"Choose an Email Client"));
        }
        catch (Exception e){
            Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
        if(isNetworkAvailable(this)) {
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
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        String time=String.valueOf(System.currentTimeMillis());
        CheckOnlineStatus(time);

    }

    public void MarkUser(View view) {
        final DatabaseReference db1= FirebaseDatabase.getInstance().getReference("MarkedUsers").child(user.getUid()).child(message);
        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        db1.child("id").setValue(message);
                        Toast.makeText(UserProfile.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
                    }
                Toast.makeText(UserProfile.this, "Already Added to Favourites", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        CheckOnlineStatus("online");


    }

    public void TakeRating(View view) {
        HashMap<String,Object> result= new HashMap<>();
        result.put("Review",String.valueOf( minimumRating.getRating()));
        databaseReference.child(message).updateChildren(result)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(minimumRating.getRating()==0.0){
                    Toast.makeText(UserProfile.this,"Please Give Some Rating" , Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(UserProfile.this,"Review Sent!" , Toast.LENGTH_SHORT).show();
                }



            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });;
        //Toast.makeText(this, String.valueOf( minimumRating.getRating()), Toast.LENGTH_SHORT).show();
    }
}
