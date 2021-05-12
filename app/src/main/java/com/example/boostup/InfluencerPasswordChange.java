package com.example.boostup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class InfluencerPasswordChange extends AppCompatActivity {
    TextInputEditText rpassId,rrpassId,rprevpassword;
    TextInputLayout rpassLayout,rrpassLayout,rprevpass_layout;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String uid;
    String pass;
    ProgressDialog pd;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_influencer_password_change);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference("user").child("Manager");
        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        uid=user.getUid();
        actionBar=getSupportActionBar();
        actionBar.setTitle("Change Password");

        rprevpassword=findViewById(R.id.rprevpassword);
        rpassId=findViewById(R.id.rpassword);
        rrpassId=findViewById(R.id.rpassword1);

        rpassLayout=findViewById(R.id.rpass_layout);
        rrpassLayout=findViewById(R.id.rrpass_layout);
        rprevpass_layout=findViewById(R.id.rprevpass_layout);
        pd=new ProgressDialog(this);

    }

    public void UpdatePassword(View view) {
        pd.setMessage("Updating Password");

        final String registerpass=rpassId.getText().toString();
        String registerrepass=rrpassId.getText().toString();
        String prevpass=rprevpassword.getText().toString();
        if(registerpass.isEmpty() && registerrepass.isEmpty() && prevpass.isEmpty()){
            rpassLayout.setError("Password cannot be Empty");
            rrpassLayout.setError("Password cannot be Empty");
            rprevpass_layout.setError("Password cannot be Empty");
        }
        else if(prevpass.isEmpty()){
            rprevpass_layout.setError("Password cannot be Empty");
            rprevpass_layout.requestFocus();
        }
        else if(registerpass.isEmpty()){
            rpassLayout.setError("Password cannot be Empty");
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
        else if(prevpass.length()<8 ||registerpass.length()<8 || registerrepass.length()<8){
            Toast.makeText(this, "Short Password", Toast.LENGTH_SHORT).show();
        }
        else {
            pd.show();
            databaseReference.orderByChild("Email").equalTo(user.getEmail()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for(DataSnapshot ds :dataSnapshot.getChildren()){
                            pass=""+ds.child("Password").getValue();
                        }
                        if(pass.equals("")){
                            pd.dismiss();
                            Toast.makeText(InfluencerPasswordChange.this, "You Cannot Change Password", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            HashMap<String,Object> result= new HashMap<>();
                            result.put("Password",registerpass);
                            databaseReference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(InfluencerPasswordChange.this, "Updated Successfully", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(InfluencerPasswordChange.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
    public void onBackPressed() {
        Intent i = new Intent(InfluencerPasswordChange.this,InfluencerSettings.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        super.onBackPressed();
    }
}
