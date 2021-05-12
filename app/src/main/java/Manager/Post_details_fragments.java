package Manager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.boostup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Post_details_fragments extends Fragment {
Button btn;
EditText title,description;
    FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,db1;
    String uid;

    public Post_details_fragments() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_post_details_fragments, container, false);
        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        firebaseDatabase= FirebaseDatabase.getInstance();
        uid=user.getUid();
        databaseReference=firebaseDatabase.getReference("Posts");

        btn = (Button) view.findViewById(R.id.addProductid);
        title = (EditText) view.findViewById(R.id.post_title);
        description = (EditText) view.findViewById(R.id.post_description);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });


        return view;
    }

    private void addProduct() {
        String postTittle = title.getText().toString();
        String postDescrption = description.getText().toString();
        Toast.makeText(getContext(), postTittle, Toast.LENGTH_SHORT).show();
        db1=databaseReference.child(title.getText().toString());
        final HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("Tittle",postTittle);
        hashMap.put("Description",postDescrption);
        hashMap.put("uid",uid);

        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    db1.updateChildren(hashMap);
                    Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_SHORT).show();

                    manager_posts_fragment fragment4=new manager_posts_fragment();
                    FragmentTransaction ft4= getActivity().getSupportFragmentManager().beginTransaction();
                    ft4.replace(R.id.content,fragment4,"");
                    ft4.commit();

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}