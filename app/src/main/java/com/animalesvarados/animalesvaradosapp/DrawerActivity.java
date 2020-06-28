package com.animalesvarados.animalesvaradosapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

@SuppressLint("Registered")
public class DrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferenceConfig sharedPreferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        sharedPreferenceConfig=new SharedPreferenceConfig(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_list)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.bringToFront();

        View headerView = navigationView.getHeaderView(0);
        TextView user = headerView.findViewById(R.id.user_name);
        user.setText("Bienvenido"+sharedPreferenceConfig.getUserName());

        TextView email = headerView.findViewById(R.id.user_email);
        email.setText(sharedPreferenceConfig.getUserEmail());


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        Toast.makeText(getApplicationContext(),"Home is selected", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_list:
                        Toast.makeText(getApplicationContext(),"Historial is selected", Toast.LENGTH_LONG).show();
                        Intent k =new Intent(DrawerActivity.this, HistorialReportesActivity.class);
                        startActivity(k);
                        break;
                    case R.id.nav_gallery:
                        Toast.makeText(getApplicationContext(),"Gallery is selected", Toast.LENGTH_LONG).show();
                        Intent i =new Intent(DrawerActivity.this, GalleryActivity.class);
                        startActivity(i);
                        break;
                    case R.id.nav_slideshow:
                        Toast.makeText(getApplicationContext(),"Slideshow is selected", Toast.LENGTH_LONG).show();
                        sharedPreferenceConfig.login_status(false);
                        sharedPreferenceConfig.saveUserName("!");
                        sharedPreferenceConfig.saveUserEmail("");
                        Intent j =new Intent(DrawerActivity.this, MainActivity.class);
                        j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(j);
                        break;
                }

                drawer.closeDrawers();

                return true;
            }
        });
    }

    public Activity getActivity(){
        return this;
    }

    public void Go_reporte(View view){
        Intent intent = new Intent(getActivity(), ReportActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
