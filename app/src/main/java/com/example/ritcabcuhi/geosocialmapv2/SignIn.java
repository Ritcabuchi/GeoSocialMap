package com.example.ritcabcuhi.geosocialmapv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ritcabcuhi.geosocialmapv2.Model.User;
import com.example.ritcabcuhi.geosocialmapv2.Model.common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {
    EditText edtPhone,edtPassword;
    TextView signIn_text;
    Button btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);
        edtPhone = (MaterialEditText)findViewById(R.id.edtPhone);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        signIn_text = (TextView)findViewById(R.id.signIn_text);

        FirebaseDatabase database =FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            mDialog.dismiss();
                            User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                            if (user.getPassword().equals(edtPassword.getText().toString())) {
//                                Toast.makeText(SignIn.this, "Sign in successfully !", Toast.LENGTH_SHORT).show();
                               Intent home = new Intent(SignIn.this, fragment.class);
                                common.currentUser = user;
                               startActivity(home);
                                       finish();
                            } else {
                                Toast.makeText(SignIn.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "User not exist in database", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        signIn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(SignIn.this, SignUp.class);
                startActivity(signUp);
            }
        });
    }
}
