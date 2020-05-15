package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup.InfluencerChatAcitivity;
import com.example.boostup.ManagerChatActivity;
import com.example.boostup.MarkedUsersActivity;
import com.example.boostup.R;
import com.squareup.picasso.Picasso;
import java.util.List;

import models.ModelUser;

public class AdapterAddUsers extends RecyclerView.Adapter<AdapterAddUsers.MyHolder>{
    Context context;
    List<ModelUser> userList;
    static String sendid;

    public AdapterAddUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override

    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_chatlist,viewGroup,false);
        return  new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        final String uid=userList.get(i).getUid();
        String userImage=userList.get(i).getImage();
        String userName=userList.get(i).getUserName();

        myHolder.nameTv.setText(userName);
        try{
            Picasso.get().load(userImage).placeholder(R.drawable.userphoto).into(myHolder.profileIv);

        }
        catch (Exception e){
            Picasso.get().load(R.drawable.userphoto).into(myHolder.profileIv);
        }
        if(userList.get(i).getOnlineStatus().equals("online")){
            myHolder.onlineStatusIv.setVisibility(View.VISIBLE);
            myHolder.onlineStatusIv.setImageResource(R.drawable.circle_online);
        }
        else{
            myHolder.onlineStatusIv.setVisibility(View.GONE);
        }
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(context, ManagerChatActivity.class);
                i.putExtra(sendid,uid);
                context.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }
    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIv,onlineStatusIv;
        TextView nameTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profileIv=itemView.findViewById(R.id.profileIv);
            onlineStatusIv=itemView.findViewById(R.id.onlineStatusIv);
            nameTv=itemView.findViewById(R.id.nameTv);
        }
    }
}
