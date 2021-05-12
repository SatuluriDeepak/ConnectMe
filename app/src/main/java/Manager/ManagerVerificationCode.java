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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class ManagerVerificationCode extends AppCompatActivity {
    String recevied,send,message,uid;
    TextInputEditText rpassId;
    FirebaseAuth mAuth;
    TextInputLayout rpassLayout;
    String phone;
    String codeSent,code,sendId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_verification_code);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        codeSent = extras.getString("code");
        uid=extras.getString("id");
        Log.i("Deepak",uid);

        //codeSent=intent.getStringExtra(ReceivedMessage);
        rpassId=findViewById(R.id.rpassword);
        rpassLayout=findViewById(R.id.rpass_layout);
       // message = intent.getStringExtra(recevied);
        mAuth= FirebaseAuth.getInstance();
        //Log.i("Code",message);
        progressDialog= new ProgressDialog(this);
    }

    public void VerifyCode(View view) {
        code = rpassId.getText().toString();
        if (code.isEmpty()) {
            rpassLayout.setError("Empty Code");
        }
        else
        {
            progressDialog.setMessage("Verifying Code");
            progressDialog.show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
            signInWithPhoneAuthCredential(credential);
        }
    }
        private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent intent=new Intent(ManagerVerificationCode.this,ManagerForgetPassword.class);
                                intent.putExtra(sendId,uid);
                                startActivity(intent);
                                //Toast.makeText(ManagerVerificationCode.this, "Entered Correct Code", Toast.LENGTH_SHORT).show();
                                //Intent intent= new Intent(ManagerVerificationCode.this,ManagerLogin.class);
                                //startActivity(intent);

                            } else {
                                Toast.makeText(ManagerVerificationCode.this, "Entered InValid Code", Toast.LENGTH_SHORT).show();

                            }
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
