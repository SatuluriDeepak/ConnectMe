package adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import models.ModelChat;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MYHolder> {

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ModelChat> chatList;
    String imageUri;
    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUri) {
        this.context = context;
        this.chatList = chatList;
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public MYHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.influencer_row_chat_right,viewGroup,false);
            return  new MYHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.influencer_row_chat_left,viewGroup,false);
            return new MYHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MYHolder Myholder, int i) {
        String message=chatList.get(i).getMessage();
        String time=chatList.get(i).getTimeStamp();

        Calendar  cal=  Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(time));
        String date= DateFormat.format("hh:mm aa",cal).toString();
        Myholder.messageTv.setText(message);
        Myholder.timeStamp.setText(date);
        /*
        try
        {
            Picasso.get().load(imageUri)
                    .into(Myholder.profileIV);

        }
        catch (Exception e){

        }
*/
        //Set Seen/Delivered Status
        if(i==chatList.size()-1){
            if(chatList.get(i).isSeenStatus()){
                Myholder.DeliverStatus.setText("Seen");
            }
            else{
                Myholder.DeliverStatus.setText("Sent");
            }
        }
        else {
            Myholder.DeliverStatus.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSenderid().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return  MSG_TYPE_LEFT;
        }
    }

    class MYHolder extends RecyclerView.ViewHolder{
        ImageView profileIV;
        TextView messageTv,timeStamp,DeliverStatus;
        public MYHolder(@NonNull View itemView) {
            super(itemView);
            //profileIV=itemView.findViewById(R.id.profileIV);
            messageTv=itemView.findViewById(R.id.messageTv);
            timeStamp=itemView.findViewById(R.id.timeStamp);
            DeliverStatus=itemView.findViewById(R.id.DeliverStatus);
        }

    }
}
