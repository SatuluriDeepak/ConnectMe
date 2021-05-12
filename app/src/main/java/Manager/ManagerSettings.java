package Manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.example.boostup.ChangePasswordManager;

import com.example.boostup.MarkedUsersActivity;
import com.example.boostup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManagerSettings extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String uid;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_settings);
        firebaseDatabase= FirebaseDatabase.getInstance();
        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        uid=user.getUid();
        databaseReference= firebaseDatabase.getReference("user").child("Managers").child(uid);
        actionBar=getSupportActionBar();
        actionBar.setTitle("Settings");
    }

    public void markedusers(View view) {
        Intent i=new Intent(ManagerSettings.this, MarkedUsersActivity.class);
        startActivity(i);
    }

    public void changePassword(View view) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String password=""+dataSnapshot.child("Password").getValue().toString();
                if(password.equals("")){
                    Toast.makeText(ManagerSettings.this, "You cannot change Password", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent i = new Intent(ManagerSettings.this, ChangePasswordManager.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendmail(View view) {
        Intent sendEmail=new Intent(Intent.ACTION_SEND);
        sendEmail.setData(Uri.parse("mailto:"));
        sendEmail.setType("text/plain");
        sendEmail.putExtra(sendEmail.EXTRA_EMAIL,new String[]{"sathulurideepak4@gmail.com"});
        try{
            startActivity(Intent.createChooser(sendEmail,"Choose an Email Client"));
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

    public void sendfeedback(View view) {
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

    public void about(View view) {
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ManagerSettings.this, Manager.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
