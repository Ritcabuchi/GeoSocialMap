package com.example.ritcabcuhi.geosocialmapv2.ui;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ritcabcuhi.geosocialmapv2.api.UserApi;
import com.example.ritcabcuhi.geosocialmapv2.api.ApiListener;
import com.example.ritcabcuhi.geosocialmapv2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;

    MaterialEditText edtEmail,edtName,edtPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtName =  findViewById(R.id.edtName);

        btnSignUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseDatabase.getInstance();

        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final String email = edtEmail.getText().toString();
        final String name = edtName.getText().toString();
        final String password = edtPassword.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(this, "กรุณากรอกอีเมลล์", Toast.LENGTH_SHORT).show();
            return;
        }

        if(name.isEmpty()){
            Toast.makeText(this, "กรุณากรอกชื่อ", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.isEmpty()){
            Toast.makeText(this, "กรุณากรอกรหัสผ่าน", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            UserApi.getInstance().createUser(name,task.getResult().getUser()).setListener(new ApiListener() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    mDialog.dismiss();
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(SignUpActivity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onFailure(DatabaseError error) {
                                    mDialog.dismiss();
                                }
                            });


                        } else {
                            mDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            String errMessage = task.getException().getLocalizedMessage();
                            if(errMessage != null)
                                Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
