package adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup.InfluencerPostViewActivity;
import com.example.boostup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import Manager.ManagerPostViewActivity;
import models.ModelPosts;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{
    List<ModelPosts> modelPosts;
    Context context;
    FirebaseAuth mAuth;
    String userId;

    public AdapterPosts(Context context,List<ModelPosts> modelPosts) {
        this.modelPosts = modelPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.produtdetails,viewGroup,false);
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        userId= user.getUid();
        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        final String tittle = modelPosts.get(i).getTittle();
        String description = modelPosts.get(i).getDescription();
        final String uid = modelPosts.get(i).getUid();
        Log.d("data",tittle+" "+description);
        myHolder.pDescription.setText(description);
        myHolder.pTittle.setText(tittle);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userId.equals(uid)){
                    Intent i = new Intent(context, ManagerPostViewActivity.class);
                    i.putExtra("tittle",tittle);
                    context.startActivity(i);
                }
                else{
                    Intent i = new Intent(context, InfluencerPostViewActivity.class);
                    i.putExtra("tittle",tittle);
                    context.startActivity(i);

                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return modelPosts.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView pTittle,pDescription;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pTittle = itemView.findViewById(R.id.productTitle);
            pDescription = itemView.findViewById(R.id.productDescription);
        }
    }
}
