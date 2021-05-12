package Manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.boostup.InfluencerLogin;
import com.example.boostup.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

//import Manager;


public class ManagerLogin extends AppCompatActivity {
    TextInputEditText lemailId,lpassId;
    Button LoginId;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener mAuthStateListener,fbAuthStateListener;
    SignInButton signInButton;
    FirebaseUser mFirebaseUser;
    TextInputLayout lemailLayout,lpassLayout;
    String SendMessage,lemail,name,lpass;
    GoogleSignInClient mGoogleSignInClient;
    String mail,ReceivedMessage;
    private int rc=1;
    private String USER="user",CHATS="Chats";
    String PersonName;
    FragmentManager fragmanetmanager;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);
        mAuth=FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference(USER).child("Managers");
        lemailId=findViewById(R.id.lemail);
        lpassId=findViewById(R.id.lpassword);
        LoginId=findViewById(R.id.login);
        lemailLayout=findViewById(R.id.lemail_layout);
        lpassLayout=findViewById(R.id.lpass_layout);
        signInButton=findViewById(R.id.sign_in);
        fragmanetmanager = getSupportFragmentManager();
        pd=new ProgressDialog(this);

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        pd.setMessage("Logging into account");

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                signIn();
            }
        });



        //Intent intent = getIntent();
        //mail = intent.getStringExtra(ReceivedMessage).toString();


        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(mFirebaseUser!=null){
                     /*
                    String mail=mFirebaseUser.getEmail();
                    DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("user");

                    Query query=dbref.child("Managers").orderByChild("Email").equalTo(mail);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()>0){
                                //Toast.makeText(ManagerLogin.this, "You are Logged In", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(ManagerLogin.this, "You Cannot Login", Toast.LENGTH_SHORT).show();
                                signOut();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });*/

                    pd.dismiss();
                    Intent i= new Intent(ManagerLogin.this,Manager.class);
                    startActivity(i);

                }
                else{
                    Toast.makeText(ManagerLogin.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
    private void signOut() {
        mAuth=FirebaseAuth.getInstance();
        mFirebaseUser=mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(ManagerLogin.this, gso);

        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    public void Register(View view) {
        Intent intent= new Intent(this, ManagerRegister.class);
        startActivity(intent);
    }

    public void EmailLoginCheck(View view) {
        lemail=lemailId.getText().toString();
        lpass=lpassId.getText().toString();

        if(lemail.isEmpty() && lpass.isEmpty()){
            lemailLayout.setError("Email Cannot Be Empty");
            lpassLayout.setError("Password Cannot be Empty");
            lemailLayout.requestFocus();
        }
        else if(lemail.isEmpty()){
            lemailLayout.setError("Email Cannot Be Empty");
            lemailLayout.requestFocus();
        }
        else if(lpass.isEmpty()){
            lpassLayout.setError("Password Cannot be Empty");
            lpassLayout.requestFocus();
        }
        else if(!(lemail.isEmpty() && lpass.isEmpty())){
            pd.show();
            DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("user");
            Query query=dbref.child("Managers").orderByChild("Email").equalTo(lemail);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount()>0){
                        mAuth.signInWithEmailAndPassword(lemail,lpass).addOnCompleteListener(ManagerLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(ManagerLogin.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                                else{
                                    pd.dismiss();
                                    Intent intoHome=new Intent(ManagerLogin.this,Manager.class);
                                    startActivity(intoHome);
                                    //FirebaseUser user=mAuth.getCurrentUser();
                                    //updateUI(user);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ManagerLogin.this,e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });
                    }
                    else{
                        pd.dismiss();
                        Toast.makeText(ManagerLogin.this, "User Not Registered", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            pd.dismiss();
            Toast.makeText(ManagerLogin.this, "Error Occured",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener!=null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
        /*
        if (fbAuthStateListener!=null){
            mAuth.removeAuthStateListener(fbAuthStateListener);
        }*/
    }
    private void signIn() {
        Intent signIntent= mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent,rc);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //pd.show();
        super.onActivityResult(requestCode, resultCode, data);
        //mcallbackManager.onActivityResult(requestCode,resultCode,data);
        if(requestCode==rc){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else{
            Toast.makeText(this, "Try,Again", Toast.LENGTH_SHORT).show();
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> Completedtask) {
        try {
            GoogleSignInAccount acc=Completedtask.getResult(ApiException.class);
            //Toast.makeText(this, "Sign in Succesfull", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (Exception e){
            Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show();
            //FirebaseGoogleAuth(null);

        }
    }
    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential authCredential= GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(ManagerLogin.this, "Successfull", Toast.LENGTH_SHORT).show();
                    FirebaseUser user=mAuth.getCurrentUser();

                    if(task.getResult().getAdditionalUserInfo().isNewUser()){
                        String email=user.getEmail();
                        String uid=user.getUid();

                        HashMap<Object,String > hashMap=new HashMap<>();
                        hashMap.put("Email",email);
                        hashMap.put("uid",uid);
                        hashMap.put("Phone","");
                        hashMap.put("Password","");
                        hashMap.put("Cover","");
                        hashMap.put("OnlineStatus","");
                        hashMap.put("Notifications","");
                        hashMap.put("Account","");
                        hashMap.put("Typing","no");
                        hashMap.put("users","");
                        hashMap.put("CompanyName","");
                        hashMap.put("UserName",user.getDisplayName());
                        hashMap.put("Image", String.valueOf(user.getPhotoUrl()));

                        firebaseDatabase= FirebaseDatabase.getInstance();
                        databaseReference.child(uid).setValue(hashMap);
                        updateUI(user);
                    }
                    pd.dismiss();
                    if(user!=null){
                        String mail=user.getEmail();
                        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("user");
                        Query query=dbref.child("Managers").orderByChild("Email").equalTo(mail);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getChildrenCount()>0){
                                    //Toast.makeText(ManagerLogin.this, "You are Logged In", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                    Intent i= new Intent(ManagerLogin.this,Manager.class);
                                    startActivity(i);

                                }
                                else{
                                    Toast.makeText(ManagerLogin.this, "You Cannot Login", Toast.LENGTH_SHORT).show();
                                    if(mAuthStateListener!=null){
                                        mAuth.removeAuthStateListener(mAuthStateListener);
                                    }
                                    signOut();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    /*
                    String email=user.getEmail();
                    String uid=user.getUid();
                    HashMap<Object,String > hashMap=new HashMap<>();
                    hashMap.put("Email",email);
                    hashMap.put("uid",uid);
                    hashMap.put("Phone","");
                    hashMap.put("Cover","");
                    hashMap.put("UserName",user.getDisplayName());
                    hashMap.put("Image", String.valueOf(user.getPhotoUrl()));
                    hashMap.put("YoutubeMoney","");
                    hashMap.put("InstagramMoney","");
                    hashMap.put("TicktokMoney","");
                    hashMap.put("InstaViews","");
                    hashMap.put("YoutubeViews","");
                    hashMap.put("TicktokViews","");
                    Log.i("pic","Image :"+user.getPhotoUrl());
                    firebaseDatabase= FirebaseDatabase.getInstance();
                    databaseReference.child(uid).setValue(hashMap);
                    updateUI(user);
                    Intent intoHome=new Intent(ManagerLogin.this,HomeActivity.class);
                    intoHome.putExtra(PersonName,name);
                    // startActivity(intoHome);*/
                }
                else{
                    pd.dismiss();
                    Toast.makeText(ManagerLogin.this, "UnSuccessfull"+task.getException(), Toast.LENGTH_SHORT).show();
                    Log.d("Fail", "Unsuccesfull : "+task.getException());
                    updateUI(null);
                }
            }

        });
    }
    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account!=null){
            PersonName=account.getDisplayName();
            String PersonGivenName=account.getGivenName();
            String PersonFamilyName=account.getFamilyName();
            String PersonEmail=account.getEmail();
            String PersonId=account.getId();
            Uri PersonPhoto=account.getPhotoUrl();
            //Toast.makeText(this, "Welcome "+PersonName, Toast.LENGTH_SHORT).show();
        }
    }

    public void ForgetPassword(View view) {
        Intent intent=new Intent(this, ManagerVerification.class);
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
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
            CheckUserStatus();
            mAuth.addAuthStateListener(mAuthStateListener);
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
            Intent i= new Intent(ManagerLogin.this, Manager.class);
            startActivity(i);

        }
        else{

        }
    }

    public void InfluencerLogin(View view) {
        Intent i = new Intent(this, InfluencerLogin.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}