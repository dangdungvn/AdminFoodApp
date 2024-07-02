package com.example.foodappadmin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import com.example.foodappadmin.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends BaseActivity {
    ActivitySignupBinding binding;
    int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariables();
    }

    private void setVariables() {
        binding.signupBtn.setOnClickListener(v -> {
            String email = binding.userEdt.getText().toString();
            String password = binding.passEdt.getText().toString();
            String rePassword = binding.passReplaceEdt.getText().toString();
            String name = binding.userNameEdt.getText().toString();

            if (password.length() < 6) {
                Toast.makeText(SignupActivity.this, "Mật khẩu phải có ít nhất 6 chữ cái", Toast.LENGTH_SHORT).show();
                return;
            } else if (!rePassword.equals(password)) {
                Toast.makeText(SignupActivity.this, "Hãy nhập lai mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            } else if (email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {
                if (task.isSuccessful()) {
                    Log.i(TAG, "onComplete: ");
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        DatabaseReference userRef = database.getReference("User");
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    int maxId = 0;
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        int currentId = Integer.parseInt(ds.getKey());
                                        if (currentId > maxId) {
                                            maxId = currentId;
                                        }
                                    }
                                    userId = maxId + 1;
                                } else {
                                    userId = 0;
                                }
                                userRef.child(String.valueOf(userId)).child("Email").setValue(email);
                                userRef.child(String.valueOf(userId)).child("Password").setValue(password);
                                userRef.child(String.valueOf(userId)).child("Name").setValue(name);
                                userRef.child(String.valueOf(userId)).child("Type").setValue(0);
                                userRef.child(String.valueOf(userId)).child("UserId").setValue(uid);
                                userRef.child(String.valueOf(userId)).child("Id").setValue(userId);
                                Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                                Toast.makeText(SignupActivity.this, "Không thể đăng ký tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Log.i(TAG, "failure: " + task.getException());
                    Toast.makeText(SignupActivity.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.btnSignin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
}