package com.example.ritcabcuhi.geosocialmapv2.UI;

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

import com.example.ritcabcuhi.geosocialmapv2.Manager.CurrentUser;
import com.example.ritcabcuhi.geosocialmapv2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edtEmail,edtPassword;
    TextView signIn_text;
    Button btnSignIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        btnSignIn = findViewById(R.id.btnSignIn);
        signIn_text = findViewById(R.id.signIn_text);

        mAuth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(this);

        signIn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(signUp);
            }
        });
    }

    @Override
    public void onClick(View view) {
        final ProgressDialog mDialog = new ProgressDialog(SignInActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        final String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.dismiss();
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    CurrentUser.getInstace().updateCurrentUser(firebaseUser.getUid());

                    Intent home = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(home);
                    finish();
                }else{
                    String message = task.getException().getLocalizedMessage();

                    if(message != null){
                        Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
