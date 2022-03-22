package com.example.familychat.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.familychat.Fragments.CallFragment;
import com.example.familychat.Fragments.ChatsFragment;
import com.example.familychat.Fragments.ContactFragment;

public class FragmentAdapter extends FragmentStateAdapter {


    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ChatsFragment();

            case 1:
                return new CallFragment();

            case 2:
                return new ContactFragment();

        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
