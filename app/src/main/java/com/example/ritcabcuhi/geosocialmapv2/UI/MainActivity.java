package com.example.ritcabcuhi.geosocialmapv2.UI;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ritcabcuhi.geosocialmapv2.EventBus.StartMainActivityEvent;
import com.example.ritcabcuhi.geosocialmapv2.Manager.CurrentUser;
import com.example.ritcabcuhi.geosocialmapv2.Model.User;
import com.example.ritcabcuhi.geosocialmapv2.R;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Fragment selectedFragment;
    private Fragment homeFragment;
    private Fragment profileFragment;

    private static final String TAG = "Fragment";

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

        setupBottomNavigation();
        setupNavigationView();
        setupToolbar();

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

    public void setupUI(){
        User currentUser = CurrentUser.getInstace().getUser();

        textUserName.setText(currentUser.getName());
        if(currentUser.getImageUrl()!=null)
            Glide.with(this).load(currentUser.getImageUrl()).into(userProfileImage);
    }


}
