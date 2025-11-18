package com.example.alpha;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

// Extends BaseActivity to inherit the common options menu.
public class AuthActivity extends BaseActivity {

    EditText ed_Email, ed_Password;
    TextView tv_Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setTitle("Auth Demo");
        Weddings(); // Reverted to original name
    }

    public void Weddings() { // Reverted to original name
        ed_Email = findViewById(R.id.ed_Email);
        ed_Password = findViewById(R.id.ed_Password);
        tv_Data = findViewById(R.id.tv_Data);
    }

    public void CreateUser_Click(View view) {
        String email = ed_Email.getText().toString().trim();
        String password = ed_Password.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(AuthActivity.this, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // As requested: Sign out the previous user before creating a new one.
        if (FBRef.refAuth.getCurrentUser() != null) {
            FBRef.refAuth.signOut();
        }

        FBRef.refAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FBRef.refAuth.getCurrentUser();
                            if (user != null) {
                                tv_Data.setText("User created: " + user.getUid());
                                Toast.makeText(AuthActivity.this, "User created successfully.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (task.getException() != null) {
                                tv_Data.setText("Authentication failed: " + task.getException().getMessage());
                            }
                            Toast.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
