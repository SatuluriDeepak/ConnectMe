package com.example.boostup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class InfluencerForgetPassword extends AppCompatActivity {
    TextInputEditText rpassId;
    TextInputLayout rpassLayout;
    Button registerId;
    FirebaseFirestore fstore;
    FirebaseAuth mAuth;
    User userM;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String phone, password;
    String codeSent,id;
    ProgressDialog progressDialog;
    int f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_forget_password);
        rpassId=findViewById(R.id.rpassword);
        rpassLayout=findViewById(R.id.rpass_layout);

        mAuth=FirebaseAuth.getInstance();
        user =mAuth.getCurrentUser();
        fstore=FirebaseFirestore.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        progressDialog= new ProgressDialog(this);
        databaseReference=firebaseDatabase.getReference("user").child("Influencers");
        Intent intent = getIntent();
        id = intent.getStringExtra(codeSent);
    }

    public void ChangePassword(View view) {
        progressDialog.setMessage("Updating Password");
        progressDialog.show();
        password=rpassId.getText().toString();
        if(password.length()<8){
            progressDialog.dismiss();
            Toast.makeText(this, "Password Length too Short", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,Object> result= new HashMap<>();
            result.put("Password",password);

            databaseReference.child(id).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(InfluencerForgetPassword.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            updateUI(null);
                            Intent intent=new Intent(InfluencerForgetPassword.this,InfluencerLogin.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(InfluencerForgetPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }
    }
    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getApplicationContext());
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
