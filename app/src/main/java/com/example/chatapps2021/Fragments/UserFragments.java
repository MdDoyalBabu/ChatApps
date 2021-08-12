package com.example.chatapps2021.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapps2021.Adapter.UserAdapter;
import com.example.chatapps2021.R;
import com.example.chatapps2021.UsersModel.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class UserFragments extends Fragment {


    private RecyclerView recyclerView;
    private List<Users> mUsers;
    private UserAdapter userAdapter;

    private EditText search_user;


    // TODO: Rename parameter arguments, choose names that match
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_fragments, container, false);

        recyclerView=view.findViewById(R.id.recyclerView_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mUsers=new ArrayList<>();

        readUser();

        search_user=view.findViewById(R.id.search_Edittext_id);

        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchUsers(s.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void searchUsers(String toString) {

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(toString)
                .endAt(toString+"\uf0ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mUsers.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){

                    Users users=snapshot1.getValue(Users.class);

                    assert users!=null;
                    assert firebaseUser!=null;

                    if(!users.getId().equals(firebaseUser.getUid())){
                        mUsers.add(users);
                    }

                }

                userAdapter=new UserAdapter(getContext(),mUsers,false);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    // search as user
    private void readUser() {


        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (search_user.getText().toString().equals("")){
                    mUsers.clear();

                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        Users users=snapshot1.getValue(Users.class);

                        assert users!=null;
                        assert firebaseUser!=null;
                        if (!users.getId().equals(firebaseUser.getUid())){
                            mUsers.add(users);
                        }

                    }

                    userAdapter=new UserAdapter(getContext(),mUsers,false);
                    recyclerView.setAdapter(userAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(), "Error"+error, Toast.LENGTH_SHORT).show();
            }
        });



    }
}