package com.example.familychat.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.example.familychat.Adapter.FragmentAdapter;
import com.example.familychat.Notification.NotificationService;
import com.example.familychat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiProvider;
import com.vanniktech.emoji.emoji.EmojiCategory;

import io.agora.rtc.RtcEngine;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FragmentStateAdapter fragmentAdapter;
    private FloatingActionButton fab_call,fab_message;
    public static boolean newChat=false;
    public static boolean newCall=false;

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
        fab_call=findViewById(R.id.main_fabbutton_call);
        fab_message=findViewById(R.id.main_fabbutton_message);
        fab_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newChat=true;
                TabLayout.Tab tab = tabLayout.getTabAt(Integer.parseInt("2"));
                tab.select();
            }
        });
        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCall=true;
                TabLayout.Tab tab = tabLayout.getTabAt(Integer.parseInt("2"));
                tab.select();
            }
        });
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
                        fab_call.setVisibility(View.GONE);
                        fab_message.setVisibility(View.VISIBLE);

                        break;
                    case 1:
                        fab_call.setVisibility(View.VISIBLE);
                        fab_message.setVisibility(View.GONE);
                        break;
                    case 2:
                        fab_call.setVisibility(View.GONE);
                        fab_message.setVisibility(View.GONE);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_settings:
                startSettingsActivity();
                break;

        }
        return true;
    }

    private void startSettingsActivity() {
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        //NotificationService.isRunning=false;
    }

    @Override
    protected void onStart() {

        super.onStart();

        //NotificationService.isRunning=true;
    }
}