package com.example.chatapps2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapps2021.Adapter.MessageAdapter;
import com.example.chatapps2021.Fragments.APIService;
import com.example.chatapps2021.Notifications.Client;
import com.example.chatapps2021.Notifications.Data;
import com.example.chatapps2021.Notifications.MyResponse;
import com.example.chatapps2021.Notifications.Sender;
import com.example.chatapps2021.Notifications.Tokan;
import com.example.chatapps2021.UsersModel.Chats;
import com.example.chatapps2021.UsersModel.Users;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {


    private TextView username;
    private CircleImageView profileImage;
    private EditText inputText;
    private ImageButton sendBtn;
    private RecyclerView recyclerView;

    private Intent intent;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String userId;
    private List<Chats> mchats;
    private MessageAdapter messageAdapter;
    ValueEventListener seenListener;

    APIService apiService;
    private boolean notify=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar=findViewById(R.id.tollbar_id_m);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        init();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        intent=getIntent();
        userId=intent.getStringExtra("userId");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userId);



        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notify=true;
                String msg=inputText.getText().toString().trim();

                if (!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userId,msg);
                }
                else {
                    Toast.makeText(getApplicationContext(), "You can't write message", Toast.LENGTH_SHORT).show();
                }

                inputText.setText("");

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Users users=snapshot.getValue(Users.class);
                    username.setText(users.getName());

                    if (users.getImageUrl().equals("default")){
                        profileImage.setImageResource(R.mipmap.ic_launcher_round);
                    }
                    else {
                        Glide.with(MessageActivity.this).load(users.getImageUrl()).into(profileImage);
                    }

                    readMessage(firebaseUser.getUid(),userId,users.getImageUrl());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });}

    public void init(){

        username=findViewById(R.id.username_m);
        profileImage=findViewById(R.id.profile_image_m);

        inputText=findViewById(R.id.input_message_m);
        sendBtn=findViewById(R.id.sendMessage_btn);
        recyclerView=findViewById(R.id.recycelrView_id_m);

    }

    private void seenMessage(String userid){

        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Chats chats=snapshot1.getValue(Chats.class);
                    if (chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(userid)){

                       HashMap<String,Object> hashMap=new HashMap<>();
                       hashMap.put("isseen",false);
                       snapshot1.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void sendMessage(String sender,String receiver,String message){


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);


        DatabaseReference chatReference=FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);

        // add user to fragment

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatReference.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final  String msg=message;

        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Users users=snapshot.getValue(Users.class);
                if (notify){
                    sendNotification(receiver,users.getName(),msg);
                }
                notify=false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void sendNotification(String receiver, String name, String msg) {

        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");

        Query query=tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

             for (DataSnapshot snapshot1:snapshot.getChildren()){
                 Tokan tokan=snapshot1.getValue(Tokan.class);
                 Data data=new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,name+": "+msg,"New Msessage",userId);

                 Sender sender=new Sender(data,tokan.getToken());

                 apiService.sendNotificaton(sender)
                         .enqueue(new Callback<MyResponse>() {
                             @Override
                             public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                 if (response.code()==200){
                                     if (response.body().success!=1){
                                         Toast.makeText(MessageActivity.this, "Faild", Toast.LENGTH_SHORT).show();
                                     }
                                 }

                             }

                             @Override
                             public void onFailure(Call<MyResponse> call, Throwable t) {

                             }
                         });
             }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void readMessage(final String myid,final String userId,final String imageUri){

        mchats=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mchats.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){

                    Chats chats=snapshot1.getValue(Chats.class);

                    if (chats.getReceiver().equals(myid) && chats.getSender().equals(userId) || chats.getReceiver().equals(userId) && chats.getSender().equals(myid) ){

                        mchats.add(chats);

                    }

                    messageAdapter=new MessageAdapter(MessageActivity.this,mchats,imageUri);
                    recyclerView.setAdapter(messageAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        seenMessage(userId);

    }

    // active status create
    private void status(String status){

        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);


    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }
}