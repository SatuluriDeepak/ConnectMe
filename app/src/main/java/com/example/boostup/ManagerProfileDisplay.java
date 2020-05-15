package com.example.boostup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ManagerProfileDisplay extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    Context context;
    String phn,sendid,senduid,message,uid,number,sendemail;
    TextView pname,pphone,pemail,companyname;
    ImageView puser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_profile_display);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        uid=user.getUid();
        pname=findViewById(R.id.pname);
        puser=findViewById(R.id.pimage);
        pphone=findViewById(R.id.pphone);
        pemail=findViewById(R.id.pemail);
        companyname=findViewById(R.id.companyname);


        Intent intent = getIntent();
        message = intent.getStringExtra(sendid);
        getAllUsers();
    }
    private void getAllUsers() {
        // FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("user").child("Managers");
        //Query query=databaseReference.orderByChild("Email").equalTo(user.getEmail());
        databaseReference.child(message).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dname=""+dataSnapshot.child("UserName").getValue().toString();
                String image =""+dataSnapshot.child("Image").getValue().toString();
                String phone=""+dataSnapshot.child("Phone").getValue().toString();
                number=""+dataSnapshot.child("Phone").getValue().toString().trim();
                String email=""+dataSnapshot.child("Email").getValue().toString();
                sendemail=""+dataSnapshot.child("Email").getValue().toString().trim();
                String companyName=""+dataSnapshot.child("CompanyName").getValue().toString();

                pname.setText(dname);
                pphone.setText(phone);
                pemail.setText(email);
                companyname.setText(companyName);

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
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void SendMessaage(View view) {
        Intent intent=new Intent(this,InfluencerChatAcitivity.class);
        intent.putExtra(senduid,message);
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

    @Override
    protected void onPause() {
        super.onPause();
        String time=String.valueOf(System.currentTimeMillis());
        CheckOnlineStatus(time);
    }
    private void CheckOnlineStatus(String status) {
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("user").child("Influencers").child(uid);
        Log.i("Userid",uid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("OnlineStatus",status);
        dbref.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckOnlineStatus("online");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CheckOnlineStatus("online");

        Intent intent= new Intent(this, InfluenerEmailHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra(send,message);
        startActivity(intent);
    }
}
