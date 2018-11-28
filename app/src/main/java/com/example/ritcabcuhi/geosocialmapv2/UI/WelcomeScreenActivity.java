package com.example.ritcabcuhi.geosocialmapv2.UI;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.ritcabcuhi.geosocialmapv2.EventBus.MainEvent;
import com.example.ritcabcuhi.geosocialmapv2.Manager.CurrentUser;
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
    public void onUpdateCurrentUser(MainEvent mainEvent){
        Intent intent = new Intent(WelcomeScreenActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


}
