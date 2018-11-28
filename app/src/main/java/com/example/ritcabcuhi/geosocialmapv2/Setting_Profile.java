package com.example.ritcabcuhi.geosocialmapv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Setting_Profile extends AppCompatActivity {
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting__profile);

        b = (Button)findViewById(R.id.btn_save);
        final EditText edtPosition = (EditText)findViewById(R.id.set_position);
        final EditText edtWorkplace = (EditText)findViewById(R.id.set_workplace);
        final EditText edtAddress = (EditText)findViewById(R.id.set_address);
        final EditText edtEmail = (EditText)findViewById(R.id.set_email);
        final EditText edtPhonenumber = (EditText)findViewById(R.id.set_phonenumber);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting_Profile.this, ProfileFragment.class);
                intent.putExtra("position",edtPosition.getText().toString());
                intent.putExtra("workplace",edtWorkplace.getText().toString());
                intent.putExtra("address",edtAddress.getText().toString());
                intent.putExtra("email",edtEmail.getText().toString());
                intent.putExtra("phonenumber",edtPhonenumber.getText().toString());
                startActivity(intent);
            }
        });
    }
}
