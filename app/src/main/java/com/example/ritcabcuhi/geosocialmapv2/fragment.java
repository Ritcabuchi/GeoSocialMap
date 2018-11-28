package com.example.ritcabcuhi.geosocialmapv2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.app.ActionBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.auth.FirebaseAuthException;


public class fragment extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Fragment selectedFragment;
    private Fragment homeFragment;
    private Fragment profileFragment;

    private static final String TAG = "Fragment";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
//        navigationView.setNavigationItemSelectedListener(this);
//
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        try{
            BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            bottomNav.setOnNavigationItemSelectedListener(navListener);
            bottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem item) { }
            });
            drawerLayout = (DrawerLayout)findViewById(R.id.drawer_nav);
            navigationView = (NavigationView)findViewById(R.id.nav_view) ;

            homeFragment = new HomeFragment();
            profileFragment = new ProfileFragment();


            selectedFragment = homeFragment;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();


        }catch(Exception e){
            Log.e(TAG, "onCreate: ",e );
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){

                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
//                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                                  getSupportActionBar().hide();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try{
            item.setChecked(true);
            switch (item.getItemId()){
                case R.id.menu_card_view:
                    Log.d(TAG, "onNavigationItemSelected: " + item.getTitle());
                    break;
                case R.id.menu_recycler_view:
                    Log.d(TAG, "onNavigationItemSelected: " + item.getTitle());
                    break;
                case R.id.menu_view_pager:
                    Log.d(TAG, "onNavigationItemSelected: " + item.getTitle());
                    break;

                case R.id.menu_logout:
                    Log.d(TAG, "onNavigationItemSelected: " + item.getTitle());
                    break;
            }
            drawerLayout.closeDrawers();
        }catch (Exception e){
            Log.e(TAG, "onNavigationItemSelected: ",e );
        }

        return false;
    }

}
