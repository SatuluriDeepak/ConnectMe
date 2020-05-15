package com.example.boostup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class InfluencerVerification extends AppCompatActivity {
    TextInputEditText rpassId;
    Button registerId;
    FirebaseFirestore fstore;
    FirebaseAuth mAuth;
    User userM;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;
    DatabaseReference databaseReference;
    TextInputLayout rpassLayout;
    String phone, pass;
    String codeSent,id;
    String sendcode;
    ProgressDialog progressDialog;
    int f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_influencer_verification);
        rpassId=findViewById(R.id.rpassword);
        rpassLayout=findViewById(R.id.rpass_layout);

        mAuth=FirebaseAuth.getInstance();
        user =mAuth.getCurrentUser();
        fstore=FirebaseFirestore.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        progressDialog= new ProgressDialog(this);
        databaseReference=firebaseDatabase.getReference("user").child("Influencers");

    }
    
    public void SendCode(View view) {
        phone = "+91" + rpassId.getText().toString();
        if (phone.length() != 13) {
            rpassLayout.setError("Invalid Number");
        } else {
            rpassLayout.setError(null);
            databaseReference.orderByChild("Phone").equalTo(phone).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for(DataSnapshot ds :dataSnapshot.getChildren()){
                            id= ""+ds.child("uid").getValue();
                            pass=""+ds.child("Password").getValue();
                            Log.i("ID",id);
                        }
                        if(pass.equals("")){
                            Toast.makeText(InfluencerVerification.this, "You Cannot Change Password", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            progressDialog.setMessage("Sending Verification Code");
                            progressDialog.show();
                            sendVerification();
                        }
                    }
                    else {
                        Toast.makeText(InfluencerVerification.this, "You Haven't Registered Phone Number", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contactadmin,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.out_menu){
            Intent intent=new Intent(InfluencerVerification.this,ContactAdminActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new
            PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                }

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeSent=s;
                    progressDialog.dismiss();
                    Toast.makeText(InfluencerVerification.this, "Code Send", Toast.LENGTH_SHORT).show();
                    Log.i("Sent",codeSent);
                    Intent intent=new Intent(InfluencerVerification.this,InfluencerVerificationCode.class);
                    Bundle extras = new Bundle();
                    extras.putString("code",codeSent);
                    extras.putString("id",id);
                    intent.putExtras(extras);
                    //intent.putExtra(sendcode,codeSent);
                    startActivity(intent);

                }

                @Override
                public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                    super.onCodeAutoRetrievalTimeOut(s);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(InfluencerVerification.this, "message : "+e, Toast.LENGTH_SHORT).show();
                    Log.w("activity","Failure "+e.toString());
                    progressDialog.dismiss();

                }
            };

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

}

