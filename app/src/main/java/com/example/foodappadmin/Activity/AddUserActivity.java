package com.example.foodappadmin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import com.example.foodappadmin.databinding.ActivityAddUserBinding;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AddUserActivity extends BaseActivity {
    ActivityAddUserBinding binding;
    private int Id;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariables();
        onClick();
        getIntentExtra();
    }


    private void onClick() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.applyBtn.setOnClickListener(view -> {
            if (binding.nameEdt.getText().toString().isEmpty()) {
                binding.nameEdt.setError("Vui lòng nhập tên");
                return;
            }
            if (binding.emailEdt.getText().toString().isEmpty()) {
                binding.emailEdt.setError("Vui lòng nhập email");
                return;
            }
            if (binding.sdtEdt.getText().toString().isEmpty()) {
                binding.sdtEdt.setError("Vui lòng nhập số điện thoại");
                return;
            }
            if (binding.diaChiEdt.getText().toString().isEmpty()) {
                binding.diaChiEdt.setError("Vui lòng nhập địa chỉ");
                return;
            }
            if (binding.passEdt.getText().toString().isEmpty()) {
                binding.passEdt.setError("Vui lòng nhập mật khẩu");
                return;
            }
            binding.progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(binding.emailEdt.getText().toString(), binding.passEdt.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        UserId = firebaseUser.getUid();
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
                                    Id = maxId + 1;
                                } else {
                                    Id = 0;
                                }
                                userRef.child(String.valueOf(Id)).child("Id").setValue(Id);
                                userRef.child(String.valueOf(Id)).child("Name").setValue(binding.nameEdt.getText().toString());
                                userRef.child(String.valueOf(Id)).child("Email").setValue(binding.emailEdt.getText().toString());
                                userRef.child(String.valueOf(Id)).child("SDT").setValue(binding.sdtEdt.getText().toString());
                                userRef.child(String.valueOf(Id)).child("DiaChi").setValue(binding.diaChiEdt.getText().toString());
                                userRef.child(String.valueOf(Id)).child("Password").setValue(binding.passEdt.getText().toString());
                                if (binding.adminCb.isChecked()) {
                                    userRef.child(String.valueOf(Id)).child("Type").setValue(0);
                                } else {
                                    userRef.child(String.valueOf(Id)).child("Type").setValue(1);
                                }
                                userRef.child(String.valueOf(Id)).child("UserId").setValue(UserId);
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(AddUserActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(AddUserActivity.this, "Không thể đăng ký tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddUserActivity.this, "Không thể đăng ký tài khoản", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setVariables() {
        binding.nameEdt.setText("");
        binding.emailEdt.setText("");
        binding.sdtEdt.setText("");
        binding.diaChiEdt.setText("");
        binding.passEdt.setText("");
        binding.adminCb.setChecked(false);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        Id = intent.getIntExtra("UserId", -1);
    }
}