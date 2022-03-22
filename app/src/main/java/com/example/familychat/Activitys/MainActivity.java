package com.example.familychat.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;


import com.example.familychat.Adapter.FragmentAdapter;
import com.example.familychat.R;
import com.example.familychat.Util.FirebaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FragmentStateAdapter fragmentAdapter;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitWidges();

    }


    private void InitWidges() {
        toolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        viewPager=findViewById(R.id.main_viewPager);
        tabLayout=findViewById(R.id.main_tablayout);
        fragmentAdapter= new FragmentAdapter(this);
        viewPager.setAdapter(fragmentAdapter);
        fab=findViewById(R.id.main_fabbutton);

        TabLayoutMediator mediator=new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch (position){
                    case 0:
                        tab.setText("Nachrichten");
                        break;
                    case 1:
                        tab.setText("Anrufe");
                        break;
                    case 2:
                        tab.setText("Kontakte");
                        break;
                }

            }
        });
        mediator.attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        fab.setVisibility(View.VISIBLE);
                        fab.setImageResource(R.drawable.ic_message);
                        break;
                    case 1:
                        fab.setVisibility(View.VISIBLE);
                        fab.setImageResource(R.drawable.ic_call);
                        break;
                    case 2:
                        fab.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }


}