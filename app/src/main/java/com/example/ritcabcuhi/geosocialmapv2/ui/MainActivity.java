package com.example.ritcabcuhi.geosocialmapv2.ui;

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
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ritcabcuhi.geosocialmapv2.eventbus.DataEditEvent;
import com.example.ritcabcuhi.geosocialmapv2.eventbus.StartMainActivityEvent;
import com.example.ritcabcuhi.geosocialmapv2.manager.CurrentUser;
import com.example.ritcabcuhi.geosocialmapv2.model.User;
import com.example.ritcabcuhi.geosocialmapv2.R;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Fragment selectedFragment;
    private Fragment homeFragment;
    private Fragment profileFragment;

    private static final String TAG = "Fragment";
    private static final String HOME_FRAGMENT = "home_fragment";
    private static final String PROFILE_FRAGMENT = "profile_fragment";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNav;
    Toolbar toolbar;

    CircleImageView userProfileImage;
    TextView textUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        EventBus.getDefault().post(new StartMainActivityEvent());

        navigationView = findViewById(R.id.nav_view);
        bottomNav =  findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);

//        if(savedInstanceState != null){
//            homeFragment = getSupportFragmentManager().getFragment(savedInstanceState,HOME_FRAGMENT);
//            profileFragment = getSupportFragmentManager().getFragment(savedInstanceState,PROFILE_FRAGMENT);
//        } else {
            homeFragment = new HomeFragment();
            profileFragment = new ProfileFragment();
//        }

        setupBottomNavigation();
        setupNavigationView();
        setupToolbar();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
//        getSupportFragmentManager().putFragment(outState, HOME_FRAGMENT, homeFragment);
//        getSupportFragmentManager().putFragment(outState,PROFILE_FRAGMENT,profileFragment);
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void setupNavigationView(){
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        userProfileImage = headerView.findViewById(R.id.userProfileImage);
        textUserName = headerView.findViewById(R.id.userName);

        setupUI();
    }

    private void setupBottomNavigation(){
        try{
            bottomNav.setOnNavigationItemSelectedListener(navListener);
            bottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem item) { }
            });
            drawerLayout = findViewById(R.id.drawer_nav);
            navigationView = findViewById(R.id.nav_view) ;

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
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(this,MenuActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            drawerLayout.closeDrawers();
        }catch (Exception e){
            Log.e(TAG, "onNavigationItemSelected: ",e );
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onUserUpdate(DataEditEvent e){
        setupUI();
    }

    public void setupUI(){
        User currentUser = CurrentUser.getInstace().getUser();

        textUserName.setText(currentUser.getName());
        if(currentUser.getImageUri()!=null)
            Glide.with(this).load(currentUser.getImageUri()).into(userProfileImage);
    }


}
