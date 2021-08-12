package com.example.chatapps2021.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapps2021.Adapter.UserAdapter;
import com.example.chatapps2021.Notifications.Tokan;
import com.example.chatapps2021.R;
import com.example.chatapps2021.UsersModel.ChatList;
import com.example.chatapps2021.UsersModel.Chats;
import com.example.chatapps2021.UsersModel.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match



   private UserAdapter userAdapter;
   private List<Users> mUsers;
   private List<ChatList> usersList;

    private RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView=view.findViewById(R.id.recyvlewView_id_fc);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);




        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final String currentUser=firebaseUser.getUid();


        usersList=new ArrayList<>();

        reference= FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usersList.clear();

                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    ChatList chatlist=snapshot1.getValue(ChatList.class);
                    usersList.add(chatlist);
                }
                chatlist();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");

        Tokan tokan=new Tokan(token);
        reference.child(firebaseUser.getUid()).setValue(tokan);
    }

    private void chatlist() {

        mUsers=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mUsers.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Users users=snapshot1.getValue(Users.class);
                    for (ChatList chatlist: usersList){
                        if (users.getId().equals(chatlist.getId())){
                            mUsers.add(users);
                        }
                    }
                }

                userAdapter=new UserAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}