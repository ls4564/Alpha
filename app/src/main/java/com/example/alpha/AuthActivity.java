package com.example.alpha;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    EditText ed_Email, ed_Password;
    TextView tv_Data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Weddings();
    }

    public void Weddings()
    {
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

        if(FBRef.refAuth.getCurrentUser() != null)
        {
            FBRef.refAuth.signOut();
        }
        FBRef.refAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = FBRef.refUser;
                            tv_Data.setText("User created: " + user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            tv_Data.setText("Authentication failed: " + task.getException().getMessage());
                            Toast.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if(FBRef.refAuth.getCurrentUser() != null)
        {
            FBRef.refAuth.signOut();
        }

        super.onDestroy();
    }
}
