package com.example.chatapps2021.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapps2021.R;
import com.example.chatapps2021.UsersModel.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;

    Context context;
    List<Chats> mChats;
    String imageUri;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chats> mChats, String imageUri) {
        this.context = context;
        this.mChats = mChats;
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewType==MSG_TYPE_RIGHT){

            view = LayoutInflater.from(context).inflate(R.layout.chat_list_right, parent, false);
        }
        else {

            view = LayoutInflater.from(context).inflate(R.layout.chat_list_left, parent, false);
        }
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chats chats=mChats.get(position);

        holder.show_message.setText(chats.getMessage());

        if (imageUri.equals("default")){

            holder.profileImage.setImageResource(R.mipmap.ic_launcher);

        }
        else {
            Glide.with(context).load(imageUri).into(holder.profileImage);
        }

        // message seen statement start

        if (position==mChats.size()-1){ // this statement is user last message check
            if (chats.isIsseen()){
                holder.message_seen.setText("Seen");
            }else {
                holder.message_seen.setText("Delivered");
            }
        }else {
            holder.message_seen.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView show_message;
        private ImageView profileImage;
        private TextView message_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message=itemView.findViewById(R.id.show_message);
            profileImage=itemView.findViewById(R.id.profileImage);
            message_seen=itemView.findViewById(R.id.text_seen);


        }
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if (mChats.get(position).getSender().equals(firebaseUser.getUid())){

            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }

    }
}
