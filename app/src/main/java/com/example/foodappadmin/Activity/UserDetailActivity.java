package com.example.foodappadmin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodappadmin.Domain.User;
import com.example.foodappadmin.R;
import com.example.foodappadmin.databinding.ActivityFoodDetailBinding;
import com.example.foodappadmin.databinding.ActivityUserDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserDetailActivity extends BaseActivity {
    ActivityUserDetailBinding binding;
    private int Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        setVariables();
    }

    private void setVariables() {
        DatabaseReference userRef = database.getReference("User");
        binding.progressBar.setVisibility(View.VISIBLE);
        Query query = userRef.orderByChild("Id").equalTo(Id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        binding.emailEdt.setText(user.getEmail());
                        binding.nameEdt.setText(user.getName());
                        binding.sdtEdt.setText(user.getSDT());
                        binding.diaChiEdt.setText(user.getDiaChi());
                        if(user.getType()==0){
                            binding.adminCb.setChecked(false);
                        } else {
                            binding.adminCb.setChecked(true);
                        }
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.btnBack.setOnClickListener(view -> finish());
        binding.applyBtn.setOnClickListener(view -> {
            if(binding.emailEdt.getText().toString().isEmpty()){
                binding.emailEdt.setError("Hãy nhập email");
                return;
            }
            if(binding.nameEdt.getText().toString().isEmpty()){
                binding.nameEdt.setError("Hãy nhập tên người dùng");
                return;
            }
            userRef.child(String.valueOf(Id)).child("Email").setValue(binding.emailEdt.getText().toString());
            userRef.child(String.valueOf(Id)).child("Name").setValue(binding.nameEdt.getText().toString());
            userRef.child(String.valueOf(Id)).child("SDT").setValue(binding.sdtEdt.getText().toString());
            userRef.child(String.valueOf(Id)).child("DiaChi").setValue(binding.diaChiEdt.getText().toString());
            if(binding.adminCb.isChecked()){
                userRef.child(String.valueOf(Id)).child("Type").setValue(1);
            } else {
                userRef.child(String.valueOf(Id)).child("Type").setValue(0);
            }
            Intent intent = new Intent(UserDetailActivity.this, ListUserActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void getIntentExtra() {
        Id = getIntent().getIntExtra("Id", -1);
    }
}