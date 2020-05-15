package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup.R;
import com.example.boostup.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

import models.ModelUser;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {
    public static String sendid;
    Context context;
    List<ModelUser> userList;

    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout(row_user)
        View view= LayoutInflater.from(context).inflate(R.layout.row_uers,viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        final String userImage= userList.get(i).getImage();
        final String userName=userList.get(i).getUserName();
        String you=userList.get(i).getYoutubeViews();
        String ins=userList.get(i).getInstaViews();
        String tic=userList.get(i).getTicktokViews();
        final String id= userList.get(i).getUid();
        //Set Data
        myHolder.uname.setText(userName);
        myHolder.iv.setText(ins);
        myHolder.yv.setText(you);
        myHolder.tv.setText(tic);

        try{
            Picasso.get().load(userImage).placeholder(R.drawable.diplay_face)
                    .into(myHolder.img);
        }
        catch (Exception e){

        }
        //Handle Item Click
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UserProfile.class);
                i.putExtra(sendid,id);
                //Toast.makeText(context, "Hello "+userName, Toast.LENGTH_SHORT).show();
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView uname,yv,iv,tv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.displayimage);
            uname=itemView.findViewById(R.id.usernameid);
            yv=itemView.findViewById(R.id.youviewid);
            iv=itemView.findViewById(R.id.insviewid);
            tv=itemView.findViewById(R.id.ticviewid);

        }
    }
}
