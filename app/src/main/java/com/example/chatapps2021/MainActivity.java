package com.example.chatapps2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatapps2021.Fragments.ChatsFragment;
import com.example.chatapps2021.Fragments.UserFragments;
import com.example.chatapps2021.Fragments.UserProfileFragment;
import com.example.chatapps2021.UsersModel.Users;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private CircleImageView circleImageView;
    private TextView username;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar=findViewById(R.id.tollbar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");




        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        circleImageView=findViewById(R.id.profile_image_id);
        username=findViewById(R.id.username_id);

        tabLayout=findViewById(R.id.table_layout);
        viewPager=findViewById(R.id.view_pager);


        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new ChatsFragment(),"Chats");
        viewPagerAdapter.addFragments(new UserFragments(),"Users");
        viewPagerAdapter.addFragments(new UserProfileFragment(),"Profile");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users=snapshot.getValue(Users.class);

                username.setText(users.getName());

                if (users.getImageUrl().equals("default")){
                    circleImageView.setImageResource(R.mipmap.ic_launcher_round);
                }
                else {
                    //  changethis

                    Glide.with(getApplicationContext()).load(users.getImageUrl()).into(circleImageView);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.logOut:

               FirebaseAuth.getInstance().signOut();
               Intent intent=new Intent(MainActivity.this,StartActivity.class);
               startActivity(intent);
                finish();

        }
        return false;

    }


    class ViewPagerAdapter extends FragmentPagerAdapter{


       private ArrayList<Fragment> fragments;
       private ArrayList<String> titles;



     ViewPagerAdapter(FragmentManager fm){
         super(fm);
         this.fragments=new ArrayList<>();
         this.titles=new ArrayList<>();

     }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragments(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

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
        status("offline");
    }
}