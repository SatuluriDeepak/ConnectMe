package com.example.boostup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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

import adapters.AdapterAddUsers;
import adapters.AdapterInfluencerChatList;
import models.ModelChatList;
import models.ModelUser;

public class MarkedUsersActivity extends AppCompatActivity {
        private FirebaseAuth mAuth;
        private FirebaseUser user;
        private RecyclerView recyclerView;
        private List<ModelChatList> chatListList;
        private List<ModelUser>userList;
        private DatabaseReference databaseReference;
        private DatabaseReference reference;
        private AdapterAddUsers adapterChatList;
        String Chat="Chats";
        FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked_users);
        recyclerView=findViewById(R.id.recyclerView);
        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        chatListList=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("MarkedUsers").child(user.getUid());
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
    }
    private void loadChats() {
        userList=new ArrayList<>();
        DatabaseReference db1=FirebaseDatabase.getInstance().getReference("user").child("Influencers");
        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelUser modelUser=ds.getValue(ModelUser.class);
                    for(ModelChatList chatList:chatListList){
                        if(modelUser.getUid()!=null && modelUser.getUid().equals(chatList.getId())){
                            userList.add(modelUser);
                            break;
                        }
                    }
                    adapterChatList=new AdapterAddUsers(MarkedUsersActivity.this,userList);
                    adapterChatList.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChatList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
