package com.example.boostup;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileEmailFragment extends Fragment {
    FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    GoogleSignInClient mGoogleSignInClient;
    StorageReference storageReference;
    String storagePath="Users_Profile_Cover_Imgs/";

    FloatingActionButton floatingActionButton;
    ProgressDialog pd;
    private static  final int CAMERA_REQUEST_CODE=100;
    private static  final int STORAGE_REQUEST_CODE=200;
    private static  final int IMAGE_PICK_GALLERY_CODE=300;
    private static  final int IMAGE_PICK_CAMERA_CODE=400;

    ImageView user_image,cover;
    TextView name,email,ym,im,tm,phoneid;
    String cameraPermission[];
    String storagePermission[];
    Uri image_uri;
    String ProfileOrCover;
    int f=0,m=0;
    private String USER="user",ReceivedMessage,mail,userId,OnlyName;


    public ProfileEmailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_email, container, false);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("user").child("Influencers");
        storageReference= FirebaseStorage.getInstance().getReference();

        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        user_image=view.findViewById(R.id.user_image);
        cover=view.findViewById(R.id.cover);
        name= view.findViewById(R.id.displayname);
        email=view.findViewById(R.id.displayemail);
        phoneid=view.findViewById(R.id.phoneid);
        ym=view.findViewById(R.id.youtubeid);
        im=view.findViewById(R.id.instagramid);
        tm=view.findViewById(R.id.ticktokid);
        pd=new ProgressDialog(getActivity());


        floatingActionButton=view.findViewById(R.id.fab);
        Query query=databaseReference.orderByChild("Email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    String confirm= ""+ds.child("Account").getValue();
                    if(confirm.equals("Delete")){
                        deleteAccount();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query=databaseReference.orderByChild("Email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    String dname=""+ds.child("UserName").getValue();
                    String demail=""+ds.child("Email").getValue();
                    String dcover=""+ds.child("Cover").getValue();
                    String image=""+ds.child("Image").getValue();
                    String you=""+ds.child("YoutubeMoney").getValue();
                    String inst=""+ds.child("InstagramMoney").getValue();
                    String tick=""+ds.child("TicktokMoney").getValue();
                    String phn= ""+ds.child("Phone").getValue();
                    if(phn.isEmpty()){
                        pd.setMessage("Updating Phone number");
                        showMoneyUpdate("Phone");
                    }
                    ym.setText(you);
                    im.setText(inst);
                    tm.setText(tick);
                    name.setText(dname);
                    email.setText(demail);
                    phoneid.setText(phn);
                    try{
                        Picasso.get().load(image).into(user_image);
                    }
                    catch (Exception e){
                        // Toast.makeText(getActivity(), "Hello "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Picasso.get().load(R.drawable.userphoto).into(user_image);

                    }
                    try{
                        Picasso.get().load(dcover).into(cover);
                    }
                    catch (Exception e){
                       // Picasso.get().load(R.drawable.coverpic).into(cover);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Updating Profile Picture");
                ProfileOrCover="Image";
                showImagePicDailouge();

            }
        });
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Updating Cover Picture");
                ProfileOrCover="Cover";
                showImagePicDailouge();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfile();
            }
        });
/*


        String id=user.getUid();
        databaseReference.child(id).child("UserName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Uname=dataSnapshot.getValue(String.class);
                name.setText(Uname);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        databaseReference.child(id).child("Email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Uname=dataSnapshot.getValue(String.class);
                email.setText(Uname);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
*/

        return view;
    }

    private void deleteAccount() {
            try{
                    AlertDialog.Builder myAlert= new AlertDialog.Builder(getActivity());
                    myAlert.setTitle("Account Deleted.........!");
                    myAlert.setMessage("Account Deleted due to less followers");
                    myAlert.setPositiveButton("OK", new
                            DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    signOut();
                                    Intent i = new Intent(getActivity(),MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    getActivity().finish();
                                }
                            });
                    myAlert.show();
            }
            catch (Exception e){
                //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();

            }

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.logout_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.out_menu){
            pd=new ProgressDialog(getActivity());
            pd.setMessage("Logging Out");
            pd.show();
            signOut();
            Intent i = new Intent(getActivity(),InfluencerLogin.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pd.dismiss();
            startActivity(i);
        }
        if(id == R.id.settings){
            Intent i = new Intent(getActivity(),InfluencerSettings.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    //to check if permission is enabled or not
    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result;
    }
    private void requstStoragePermission(){
        requestPermissions(storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraePermission(){

        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }
    private void requstCameraPermission(){
        requestPermissions(cameraPermission,CAMERA_REQUEST_CODE);
    }

    private void showEditProfile() {
        String []options={"Edit Phone number","Edit Instagram","Edit Youtube","Edit Ticktok"};
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    pd.setMessage("Updating Phone number");
                    showMoneyUpdate("Phone");
                }
                else if(which==1){
                    pd.setMessage("Updating Instagram Money");
                    showMoneyUpdate("InstagramMoney");

                }
                else if(which==2){
                    pd.setMessage("Updating Youtube Money");
                    showMoneyUpdate("YoutubeMoney");
                }
                else if(which==3){
                    pd.setMessage("Updating Ticktok Money");
                    showMoneyUpdate("TicktokMoney");
                }

            }
        });
        builder.create().show();

    }
    private void showMoneyUpdate(final String key) {
        if(getActivity() != null){
            final AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
            builder.setTitle("Update "+key);
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(10,10,10,10);
            if(key=="Phone"){
                builder.setTitle("Update "+key+" Number");
                final EditText editText= new EditText(getActivity());
                editText.setHint("Enter "+key);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                linearLayout.addView(editText);
                builder.setView(linearLayout);
                builder.setPositiveButton("Update ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = editText.getText().toString().trim();
                        if(value.length()!=10){
                            Toast.makeText(getContext(), "Enter valid Number", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            value="+91"+value;
                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("user").child("Managers");
                            ref.orderByChild("Phone").equalTo(value).addValueEventListener(new ValueEventListener(){
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot){
                                    if(dataSnapshot.exists()) {
                                        f=0;
                                        // Toast.makeText(getContext(), "Phone Number already exists", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        f=1;
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("user").child("Influencers");
                            dbref.orderByChild("Phone").equalTo(value).addValueEventListener(new ValueEventListener(){
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot){
                                    if(dataSnapshot.exists()) {
                                        m=0;
                                        // Toast.makeText(getContext(), "Phone Number already exists", Toast.LENGTH_SHORT).show();
                                        //showMoneyUpdate("Phone");
                                    }
                                    else{
                                        m=1;
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            if(f==1 && m==1){
                                if(!TextUtils.isEmpty(value)){
                                    pd.show();
                                    HashMap<String,Object> result= new HashMap<>();
                                    result.put(key,value);
                                    databaseReference.child(user.getUid()).updateChildren(result)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    pd.dismiss();
                                                    Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    pd.dismiss();
                                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                                else{
                                    Toast.makeText(getActivity(), "Please Enter "+key, Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(getContext(), "Phone Number already exists", Toast.LENGTH_SHORT).show();
                                showMoneyUpdate("Phone");
                            }

                        }
                    }
                });
                builder.create().show();


            }
            else if((!key.equals("Phone"))){
                final EditText editText= new EditText(getActivity());
                editText.setHint("Enter "+key);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                linearLayout.addView(editText);
                builder.setView(linearLayout);
                builder.setPositiveButton("Update ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = editText.getText().toString().trim();
                        int count=Integer.parseInt(value);
                        if (count < 1000) {
                            value= count+" r";
                        } else{
                            int exp = (int) (Math.log(count) / Math.log(1000));
                            value=String.format("%.1f %c", count / Math.pow(1000, exp),"kMGTPE".charAt(exp-1));
                        }
                        if(!TextUtils.isEmpty(value)){
                            pd.show();
                            HashMap<String,Object> result= new HashMap<>();
                            result.put(key,value);
                            databaseReference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                        else{
                            Toast.makeText(getActivity(), "Please Update "+key, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();

            }
        }


    }

    private void showImagePicDailouge() {
        String []options={"Camera","Gallery"};
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    if(!checkCameraePermission()){
                        requstCameraPermission();
                    }
                    else{
                        pickFromCamera();

                    }
                }
                else if(which==1){
                    if(!checkStoragePermission()){
                        requstStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }

                }

            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(getActivity(), "Please Enable Camera & Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
            //picking from gallery
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if( writeStorageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(getActivity(), "Please Enable Camera Permission", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode== RESULT_OK){
            if(requestCode== IMAGE_PICK_GALLERY_CODE){
                image_uri=data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
            if(requestCode== IMAGE_PICK_CAMERA_CODE){
                image_uri=data.getData();
                uploadProfileCoverPhoto(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        try{
            pd.show();
            String filePathandName=storagePath+ ""+ProfileOrCover+"_"+user.getUid();
            Log.i("path",filePathandName);
            StorageReference storageReference1= storageReference.child(filePathandName);
            storageReference1.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Image is uploaded to storage,now get its url and store in users database;
                            Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloaduri = uriTask.getResult();
                            Log.i("path","URI "+ String.valueOf(downloaduri));
                            //Log.i("Tag", String.valueOf(downloaduri));
                            //Check if image is uploaded or not and url is received
                            if(uriTask.isSuccessful()){
                                //Image uploaed
                                //Add image to user databse;
                                HashMap<String,Object> result = new HashMap<>();
                                result.put(ProfileOrCover,downloaduri.toString());
                                databaseReference.child(user.getUid()).updateChildren(result)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Url is added to database
                                                pd.dismiss();
                                                if(ProfileOrCover=="Image"){
                                                    Toast.makeText(getActivity(), "Profile Pic Updated Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Toast.makeText(getActivity(), "Cover Pic Updated Successfully", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Error
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Error Updating ....."+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                            else{
                                pd.dismiss();
                                Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("tag","Hello  "+e.getMessage());
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Tag",e.getMessage());
            Log.e("tag","Hello  "+e.getMessage());
            pd.dismiss();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {
        ContentValues contentValues= new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

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
        if(isNetworkAvailable(getContext())) {
        }
        else{
            androidx.appcompat.app.AlertDialog.Builder myAlert = new androidx.appcompat.app.AlertDialog.Builder(getContext());
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
    private void signOut() {
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getActivity());
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
}
