
package com.example.boostup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import adapters.AdapterChat;
import models.ModelChat;
import models.ModelUser;
import notifications.Data;
import notifications.Respone;
import notifications.Sender;
import notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;


public class InfluencerChatAcitivity extends AppCompatActivity {
    Toolbar mToolbar;
    ImageView mprofileIv;
    RecyclerView mchatmessage_recyle;
    TextView mnameid,mstatusid;
    EditText mEditMessage;
    ImageButton mSendButton;
    String recevied,send,message,uid;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    ArrayList<ModelChat> chatList;
    AdapterChat adapterChat;
    String hisImage;
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    private LinearLayoutManager mLayoutManager;
    private Context context;
    String msg;

    RequestQueue requestQueue;
    boolean notify =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_chat);

        Intent intent = getIntent();
        message = intent.getStringExtra(recevied);
        Log.i("Tag","ID : -"+message);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        uid=user.getUid();
        //Log.i("Tag","Current User "+uid);
        databaseReference= FirebaseDatabase.getInstance().getReference("user").child("Managers");


        Toolbar mToolbar=findViewById(R.id.mToolbar);
        mLayoutManager = new LinearLayoutManager(this);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        mprofileIv=findViewById(R.id.mprofileIv);
        mchatmessage_recyle=findViewById(R.id.mchatmessage_recyle);
        mnameid=findViewById(R.id.mnameid);
        mstatusid=findViewById(R.id.mstatusid);
        mEditMessage=findViewById(R.id.mEditMessage);
        mSendButton=findViewById(R.id.mSendButton);

        requestQueue= Volley.newRequestQueue(getApplicationContext());

        DisplayData();
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                msg=mEditMessage.getText().toString().trim();
                if(TextUtils.isEmpty(msg)){
                    Toast.makeText(InfluencerChatAcitivity.this, "Empty message cannot send", Toast.LENGTH_SHORT).show();
                }
                else {
                    SendMessage(msg);
                }
            }

        });

        ReadMessage();
        seenMessage();

    }

    private void ReadMessage() {
        chatList =new ArrayList<>();
        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("user").child("ManagerChats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat= ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(uid) && chat.getSenderid().equals(message) ||
                            chat.getReceiver().equals(message) && chat.getSenderid().equals(uid)){
                        chatList.add(chat);
                    }
                    adapterChat=new AdapterChat(InfluencerChatAcitivity.this,chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                    mchatmessage_recyle.setLayoutManager(mLayoutManager);
                    mLayoutManager.setStackFromEnd(true);
                    mchatmessage_recyle.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void seenMessage() {
        userRefForSeen=FirebaseDatabase.getInstance().getReference("user").child("ManagerChats");
        seenListener=userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat= ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(uid) && chat.getSenderid().equals(message)){
                        HashMap<String,Object> hasSeenMap= new HashMap<>();
                        hasSeenMap.put("SeenStatus",true);
                        ds.getRef().updateChildren(hasSeenMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayData() {
        databaseReference.child(message).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dname=""+dataSnapshot.child("UserName").getValue().toString();
                hisImage =""+dataSnapshot.child("Image").getValue().toString();
                String status=""+dataSnapshot.child("OnlineStatus").getValue().toString();
                String type=""+dataSnapshot.child("Typing").getValue().toString();
                mnameid.setText(dname);
                if(type.equals(uid)){
                    mstatusid.setText("typing.....");
                }
                else{
                    if(status.equals("online")){
                        mstatusid.setText(status);
                    }
                    else{
                        Calendar cal=  Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(status));
                        String date= DateFormat.format("dd/MM/yy hh:mm aa",cal).toString();
                        mstatusid.setText("last seen at :"+date);
                    }
                }
                try{
                    Picasso.get().load(hisImage).into(mprofileIv);
                }
                catch (Exception e){
                    // Toast.makeText(getActivity(), "Hello "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Picasso.get().load(R.drawable.userphoto).into(mprofileIv);

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void GoBack(View view) {
        Intent intent= new Intent(this, InfluenerEmailHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         //intent.putExtra(send,message);
        startActivity(intent);
    }

    private void SendMessage(final String msg) {
        DatabaseReference db1=FirebaseDatabase.getInstance().getReference("user");
        String timeStamp= String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("Senderid",uid);
        hashMap.put("Receiver",message);
        hashMap.put("Message",msg);
        hashMap.put("TimeStamp",timeStamp);
        hashMap.put("SeenStatus",false);
        db1.child("ManagerChats").push().setValue(hashMap);
        mEditMessage.setText("");

        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("user").child("Influencers").child(user.getUid());
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                String Image = ds.child("Image").getValue(String.class);
                String UserName = ds.child("UserName").getValue(String.class);
                String CompanyName=ds.child("CompanyName").getValue(String.class);
                String Email=ds.child("Email").getValue(String.class);
                String Account = ds.child("Account").getValue(String.class);
                String uid=ds.child("uid").getValue(String.class);
                ModelUser modelUser = new ModelUser(Image, UserName, CompanyName, Email,uid,Account);
                if(notify){
                   // Toast.makeText(InfluencerChatAcitivity.this, "Hello Bro", Toast.LENGTH_SHORT).show();
                    sendNotification(message,modelUser.getUserName(),msg);
                }
                notify=false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final DatabaseReference dbref1=FirebaseDatabase.getInstance().getReference("ChatList").child(uid).child(message);
        dbref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    dbref1.child("id").setValue(message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference dbref2=FirebaseDatabase.getInstance().getReference("ChatList").child(message).child(uid);
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    dbref2.child("id").setValue(uid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(final String hisUId, final String userName, final String msg1) {
        DatabaseReference allTokens= FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=allTokens.orderByKey().equalTo(hisUId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Token token=ds.getValue(Token.class);
                    Data data= new Data(uid,userName+" :   "+msg1,"New Message",hisUId,R.drawable.mainlogo);
                    Sender sender=new Sender(data,token.getToken());

                    try {
                        JSONObject senderJSONObject =new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJSONObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i("onResponse","Fuck "+response);
                                       // Toast.makeText(InfluencerChatAcitivity.this, response.toString(), Toast.LENGTH_SHORT).show();

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("onResponse","Error "+error);
                                Toast.makeText(InfluencerChatAcitivity.this, error.toString(), Toast.LENGTH_SHORT).show();

                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String>headers=new HashMap<>();
                                headers.put("Content-type","application/json");
                                headers.put("Authorization","key=AAAAcL0DTbU:APA91bH4QjIOGZcD6IJWfIwf2gZkIcZT4mOb57gzB4-nAgm6ziPwtEUVlvaDmgzxOX1JfXTx0rxXJyZYP10vewdoPutqitcrVxHscLaJ2U2q8Siz1DqDSvx0GmrgK-a6BfOFnMp3SR0_");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @Override
    protected void onStop() {
        super.onStop();
        String time=String.valueOf(System.currentTimeMillis());
        CheckOnlineStatus(time);
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        userRefForSeen.removeEventListener(seenListener);
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
            CheckUserStatus();

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
    private void CheckUserStatus() {
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){

        }
        else{
            startActivity(new Intent(InfluencerChatAcitivity.this,InfluencerLogin.class));
            finish();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        CheckOnlineStatus("online");
    }

}

