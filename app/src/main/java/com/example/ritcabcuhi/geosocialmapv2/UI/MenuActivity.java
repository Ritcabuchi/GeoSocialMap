package com.example.ritcabcuhi.geosocialmapv2.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.ritcabcuhi.geosocialmapv2.EventBus.MainEvent;
import com.example.ritcabcuhi.geosocialmapv2.EventBus.StartMainActivityEvent;
import com.example.ritcabcuhi.geosocialmapv2.Manager.CurrentUser;
import com.example.ritcabcuhi.geosocialmapv2.R;
import com.google.firebase.auth.FirebaseAuth;

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainStart(StartMainActivityEvent event){
        finish();
    }
}
