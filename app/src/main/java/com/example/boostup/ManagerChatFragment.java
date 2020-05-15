package com.example.boostup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.sql.StatementEvent;

import adapters.AdapterChatList;
import adapters.AdapterInfluencerChatList;
import models.ModelChat;
import models.ModelChatList;
import models.ModelUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManagerChatFragment extends Fragment {
    LinearLayout nouserid;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog pd;
    private RecyclerView recyclerView;
    private List<ModelChatList>chatListList;
    private List<ModelUser>userList;
    private DatabaseReference databaseReference;
    private DatabaseReference reference;
    String Chat= "Chats";
    FloatingActionButton floatingActionButton;
    private AdapterChatList adapterChatList;

    public ManagerChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manager_chat, container, false);
        nouserid=view.findViewById(R.id.nouserid);
        recyclerView=view.findViewById(R.id.recyclerView);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        chatListList=new ArrayList<>();
        floatingActionButton=view.findViewById(R.id.fab);
        databaseReference= FirebaseDatabase.getInstance().getReference("ChatList").child(user.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChatList modelChatList=ds.getValue(ModelChatList.class);
                    chatListList.add(modelChatList);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManagerUsersFragment fragment4=new ManagerUsersFragment();
                FragmentTransaction ft4= getActivity().getSupportFragmentManager().beginTransaction();
                ft4.replace(R.id.content,fragment4,"");
                ft4.commit();
            }
        });
        return  view;
    }

    private void loadChats() {
        nouserid.setVisibility(View.GONE);
        userList=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference("user").child("Influencers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){

                    String Image = ds.child("Image").getValue(String.class);
                    String UserName = ds.child("UserName").getValue(String.class);
                    String YoutubeViews = ds.child("YoutubeViews").getValue(String.class);
                    String TicktokViews = ds.child("TicktokViews").getValue(String.class);
                    String InstaViews =  ds.child("InstaViews").getValue(String.class);
                    String Account = ds.child("Account").getValue(String.class);
                    String uid=ds.child("uid").getValue(String.class);


                    ModelUser modelUser = new ModelUser(Image, UserName, YoutubeViews, TicktokViews, InstaViews,uid,Account);
                    for(ModelChatList chatList:chatListList){
                        if(modelUser.getUid()!=null && modelUser.getUid().equals(chatList.getId())
                                && !modelUser.getAccount().equals("Delete")){
                            nouserid.setVisibility(View.GONE);
                            userList.add(modelUser);
                            break;
                        }
                    }
                    if(userList.size()==0){
                        //userList.clear();
                        nouserid.setVisibility(View.VISIBLE);
                    }
                    else{
                        nouserid.setVisibility(View.GONE);
                        adapterChatList=new AdapterChatList(getContext(),userList);
                        adapterChatList.notifyDataSetChanged();
                        recyclerView.setAdapter(adapterChatList);
                        for(int i=0;i<userList.size();i++){
                            Log.i("Size", String.valueOf(userList.size()));
                          //  lastMessage(userList.get(i).getUid());
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String userID) {
        reference=FirebaseDatabase.getInstance().getReference(Chat);
       reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastMessage="default";
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if(chat==null){
                        continue;
                    }
                    String sender=chat.getSenderid();
                    String receiver=chat.getReceiver();
                    if(sender==null || receiver==null){
                        continue;
                    }
                    if(chat.getReceiver().equals(user.getUid()) &&
                            chat.getSenderid().equals(userID) ||
                    chat.getReceiver().equals(userID) &&
                    chat.getSenderid().equals(user.getUid())){
                        lastMessage=chat.getMessage();

                    }
                }
                adapterChatList.setLastMessageMap(userID,lastMessage);
                adapterChatList.notifyDataSetChanged();
                recyclerView.setAdapter(adapterChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
            Intent i = new Intent(getActivity(),ManagerLogin.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pd.dismiss();
            startActivity(i);
        }
        if(id == R.id.settings){
            Intent i = new Intent(getActivity(),ManagerSettings.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
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

}
