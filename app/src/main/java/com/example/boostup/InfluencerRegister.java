package com.example.boostup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class InfluencerRegister extends AppCompatActivity {
    TextInputEditText remailId,rpassId,rrpassId,rusernameId;
    Button registerId;
    FirebaseFirestore fstore;
    FirebaseAuth mAuth;
    User userM;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    TextInputLayout remailLayout,rpassLayout,rrpassLayout,rusernamelyout;
    String userId,registerusername,registeremail;
    private String USER="user";
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_influencer_register);

        remailId=findViewById(R.id.remail);
        rpassId=findViewById(R.id.rpassword);
        rrpassId=findViewById(R.id.rpassword1);
        rusernameId=findViewById(R.id.rusername);
        registerId=findViewById(R.id.register);

        rusernamelyout=findViewById(R.id.ruser_layout);
        remailLayout=findViewById(R.id.remail_layout);
        rpassLayout=findViewById(R.id.rpass_layout);
        rrpassLayout=findViewById(R.id.rrpass_layout);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser =mAuth.getCurrentUser();
        fstore=FirebaseFirestore.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference(USER).child("Influencers");
        pd=new ProgressDialog(this);

    }

    public void RegisterUser(View view) {
        pd.setMessage("Registering Account");
        registerusername=rusernameId.getText().toString();
        registeremail=remailId.getText().toString();
        final String registerpass=rpassId.getText().toString();
        String registerrepass=rrpassId.getText().toString();

        if(registeremail.isEmpty() && registeremail.isEmpty() &&
                registerpass.isEmpty() && registerrepass.isEmpty()){
            rusernamelyout.setError("Username cannot be Empty");
            remailLayout.setError("Email cannot be Empty");
            rpassLayout.setError("Password cannot be Empty");
            rrpassLayout.setError("Password cannot be Empty");
            remailLayout.requestFocus();
        }
        else if(registerusername.isEmpty()){
            rusernamelyout.setError("Username cannot be Empty");
            rusernamelyout.requestFocus();

        }
        else if(registeremail.isEmpty()){
            rusernamelyout.setError(null);
            remailLayout.setError("Email cannot be Empty");
            remailLayout.requestFocus();
        }
        else if(registerpass.isEmpty()){
            remailLayout.setError(null);
            rpassLayout.setError("Password cannot be Empty");
            rpassLayout.requestFocus();
        }
        else if(registerpass.length()<8){
            rpassLayout.setError("Password too Short");
            rpassLayout.requestFocus();
        }
        else if(registerrepass.isEmpty()){
            rrpassLayout.setError("Password cannot be Empty");
            rrpassLayout.requestFocus();
        }
        else if(!registerpass.equals(registerrepass)){
            rrpassLayout.setError("Password doesn't match");
            rrpassLayout.requestFocus();
        }
        else if(!(registeremail.isEmpty() && registerpass.isEmpty())){
            pd.show();
            mAuth.createUserWithEmailAndPassword(registeremail,registerpass).addOnCompleteListener(InfluencerRegister.this,
                    new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                            if(!(task.isSuccessful())){
                                pd.dismiss();
                                Toast.makeText(InfluencerRegister.this, "Unsuccesfull !"+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                pd.dismiss();
                                writeNewUser(registeremail,registerusername,registerpass);
                                //Goback();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(InfluencerRegister.this, "Error Occured",
                    Toast.LENGTH_SHORT).show();

        }
    }
    public void Goback(){
        Intent i = new Intent(this, InfluencerLogin.class);
        //i.putExtra(SendName,name);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

    }
    public void Launch(View view) {
        Intent i = new Intent(this,InfluencerLogin.class);
        //i.putExtra(SendName,registerusername);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    public void writeNewUser(String Email, final String UserName, final String Password) {
        firebaseUser=mAuth.getCurrentUser();
        final String email=firebaseUser.getEmail();
        final String uid=firebaseUser.getUid();

        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("user").child("Influencers");
        dbref.orderByChild("UserName").equalTo(UserName).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                if(dataSnapshot.exists()) {
                    Toast.makeText(InfluencerRegister.this, "UserName already exists", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }
                else{
                    HashMap<Object,String > hashMap=new HashMap<>();
                    hashMap.put("Email",email);
                    hashMap.put("uid",uid);
                    hashMap.put("UserName",UserName);
                    hashMap.put("Password",Password);
                    hashMap.put("Phone","");
                    hashMap.put("Image","");
                    hashMap.put("Cover","");
                    hashMap.put("Review","");
                    hashMap.put("OnlineStatus","");
                    hashMap.put("Typing","no");
                    hashMap.put("YoutubeMoney","0r");
                    hashMap.put("InstagramMoney","0r");
                    hashMap.put("TicktokMoney","0r");
                    hashMap.put("InstaViews","");
                    hashMap.put("YoutubeViews","");
                    hashMap.put("TicktokViews","");
                    hashMap.put("Account","");
                    firebaseDatabase=FirebaseDatabase.getInstance();
                    databaseReference.child(uid).setValue(hashMap);
                    Toast.makeText(InfluencerRegister.this, "Account Created!",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

