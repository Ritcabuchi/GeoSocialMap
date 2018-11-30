package com.example.ritcabcuhi.geosocialmapv2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.ritcabcuhi.geosocialmapv2.eventbus.StartMainActivityEvent;
import com.example.ritcabcuhi.geosocialmapv2.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MenuActivity extends AppCompatActivity {
        Button btn_SignIn,btn_System;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_auten);

            btn_SignIn = findViewById(R.id.btn_signin);
            btn_System = findViewById(R.id.btn_system);

            btn_SignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signUp = new Intent(MenuActivity.this, SignUpActivity.class);
                    startActivity(signUp);
                }
            });

            btn_System.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signIn = new Intent(MenuActivity.this, SignInActivity.class);
                    startActivity(signIn);
                }
            });
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
    public void onMainStart(StartMainActivityEvent event){
        finish();
    }
}
