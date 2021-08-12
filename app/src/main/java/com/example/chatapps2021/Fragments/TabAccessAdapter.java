package com.example.chatapps2021.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccessAdapter extends FragmentPagerAdapter {


    public TabAccessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

                case 0:
                UserFragments userFragments=new UserFragments();
                return userFragments;

                case 1:
                ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;

            default:
                return null;

        }


    }

    @Override
    public int getCount() {
        return 2;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){

            case 0:
                return "Users";
            case 1:
                return "Chats";

            default:
                return null;

        }
    }
}
