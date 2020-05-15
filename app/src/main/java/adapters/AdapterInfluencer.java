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
import com.example.boostup.ManagerProfileDisplay;
import com.example.boostup.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import models.ModelUser;

public class AdapterInfluencer extends RecyclerView.Adapter<AdapterInfluencer.MyHolder1> {
    public static String sendid;
    Context context;
    List<ModelUser> userList;

    public AdapterInfluencer(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder1 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout(row_user)
        View view= LayoutInflater.from(context).inflate(R.layout.influencer_row,viewGroup,false);

        return new MyHolder1(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder1 myHolder, int i) {
        //get data
        final String userImage= userList.get(i).getImage();
        final String userName=userList.get(i).getUserName();
        final String id= userList.get(i).getUid();
        String cn=userList.get(i).getCompanyName();
        String email=userList.get(i).getEmail();

        //Set Data
        myHolder.uname.setText(userName);
        myHolder.email_display.setText(email);
        myHolder.companyname_display.setText(cn);

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
                Intent i = new Intent(context , ManagerProfileDisplay.class);
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

    class MyHolder1 extends RecyclerView.ViewHolder{
        ImageView img;
        TextView uname,email_display,companyname_display;

        public MyHolder1(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.displayimage);
            uname=itemView.findViewById(R.id.usernameid);
            email_display=itemView.findViewById(R.id.email_display);
            companyname_display=itemView.findViewById(R.id.companyname_display);


        }
    }
}
