package com.example.chatapps2021.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapps2021.MessageActivity;
import com.example.chatapps2021.R;
import com.example.chatapps2021.UsersModel.Chats;
import com.example.chatapps2021.UsersModel.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<Users> usersList;
    private boolean isChart;

    String thelastMessage;



    public UserAdapter(Context context, List<Users> usersList,boolean isChart) {
        this.context = context;
        this.usersList = usersList;
        this.isChart = isChart;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view= LayoutInflater.from(context).inflate(R.layout.user_list_layout,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {


      final Users users=usersList.get(position);

        holder.userName.setText(users.getName());

        if (users.getImageUrl().equals("default")){
            holder.circleImageView.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(context).load(users.getImageUrl()).into(holder.circleImageView);
        }
        if (isChart){
            lastMessage(users.getId(),holder.last_message);
        }
        else {
            holder.last_message.setVisibility(View.GONE);
        }

        

        if (isChart){
            if (users.getStatus().equals("online")){
                   holder.active_on.setVisibility(View.VISIBLE);
                   holder.active_off.setVisibility(View.GONE);
            }
            else {
                holder.active_on.setVisibility(View.GONE);
                holder.active_off.setVisibility(View.VISIBLE);
            }
        }else {
            holder.active_on.setVisibility(View.GONE);
            holder.active_off.setVisibility(View.GONE);
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("userId",users.getId());
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private CircleImageView circleImageView;
        private CircleImageView active_on;
        private CircleImageView active_off;
        private TextView last_message;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.username_list);
            circleImageView=itemView.findViewById(R.id.profile_user);
            active_on=itemView.findViewById(R.id.active_on);
            active_off=itemView.findViewById(R.id.active_off);
            last_message=itemView.findViewById(R.id.last_message);


        }
    }

    private void lastMessage(String userid,TextView last_message){

        thelastMessage="default";


        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Chats chats=snapshot1.getValue(Chats.class);

                    if(chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(userid) ||
                            chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(firebaseUser.getUid())){
                        thelastMessage=chats.getMessage();

                    }


                }
                switch (thelastMessage){
                    case "default":
                        last_message.setText("No message");
                        break;

                    default:
                        last_message.setText(thelastMessage);
                        break;
                }
                thelastMessage="default";


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


}
