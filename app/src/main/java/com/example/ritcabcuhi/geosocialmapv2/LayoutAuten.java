package com.example.ritcabcuhi.geosocialmapv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LayoutAuten extends AppCompatActivity {
        Button btn_SignIn,btn_System;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_auten);
            btn_SignIn = (Button)findViewById(R.id.btn_signin);
            btn_System = (Button)findViewById(R.id.btn_system);


            btn_SignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signUp = new Intent(com.example.ritcabcuhi.geosocialmapv2.LayoutAuten.this, SignUp.class);
                    startActivity(signUp);
                }
            });

            btn_System.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signUp = new Intent(com.example.ritcabcuhi.geosocialmapv2.LayoutAuten.this, SignIn.class);
                    startActivity(signUp);
                }
            });
        }

}
