package com.example.wcfp_mc;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //dark mode check
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean DKmode = settings.getBoolean("DarkMode", false);
        if (DKmode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_about, R.id.nav_category, R.id.nav_mylist, R.id.nav_history)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        TextView textView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_title);
        textView.setOnClickListener(view -> {
            String user_name = settings.getString("user_name", "");
            if (user_name.equals("")) {
                navController.navigate(R.id.action_to_login);
            } else {
                navController.navigate(R.id.action_to_logout);
            }
            drawer.closeDrawer(GravityCompat.START, false);
        });
        String username = settings.getString("user_name", "");
        if (!username.equals("")) {
            textView.setText(String.format(Locale.getDefault(), "歡迎，%1$s", username));
        }

        Intent intent = getIntent();
        if (intent.getData() != null) {
            String path = intent.getData().toString();
            System.out.println("get data " + path);
            Bundle bundle = new Bundle();
            bundle.putString("url", path);
            if (path.contains("showcfp")) {
                navController.navigate(R.id.action_to_cfp, bundle);
            } else if (path.contains("call?conference")) {
                bundle.putString("name", intent.getData().getQueryParameter("conference"));
                navController.navigate(R.id.action_to_cfps, bundle);
            }
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}