package com.example.ritcabcuhi.geosocialmapv2.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.ritcabcuhi.geosocialmapv2.api.GoogleDirectionApi;
import com.example.ritcabcuhi.geosocialmapv2.api.GooglePlaceApi;
import com.example.ritcabcuhi.geosocialmapv2.eventbus.DataEditEvent;
import com.example.ritcabcuhi.geosocialmapv2.manager.CurrentUser;
import com.example.ritcabcuhi.geosocialmapv2.R;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WelcomeScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleDirectionApi.getInstance().setApiKey(getString(R.string.google_maps_key));
        GooglePlaceApi.getInstace().setApiKey(getString(R.string.google_maps_key));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
//        getSupportActionBar().hide();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            CurrentUser.getInstace().updateCurrentUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WelcomeScreenActivity.this,MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            },SPLASH_TIME_OUT);
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateCurrentUser(DataEditEvent dataEditEvent){
        Intent intent = new Intent(WelcomeScreenActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


}
