package com.example.foodappadmin.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import com.example.foodappadmin.Domain.User;
import com.example.foodappadmin.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewFoods();
        viewCategory();
        getUserName();
        signOut();
        viewUser();
        viewLocation();
        viewTime();
        viewPrice();
    }

    private void viewPrice() {
        binding.priceCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PriceActivity.class);
            startActivity(intent);
        });
    }

    private void viewTime() {
        binding.timeCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TimeActivity.class);
            startActivity(intent);
        });
    }

    private void viewLocation() {
        binding.locationCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LocationActivity.class);
            startActivity(intent);
        });
    }

    private void viewUser() {
        binding.userCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListUserActivity.class);
            startActivity(intent);
        });
    }

    private void viewCategory() {
        binding.categoryCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListCategoryActivity.class);
            startActivity(intent);
        });
    }

    private void viewFoods() {
        binding.foodCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
            startActivity(intent);
        });
    }
    private void getUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference userRef = database.getReference("User");
            Query query = userRef.orderByChild("UserId").equalTo(uid);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            User userData = ds.getValue(User.class);
                            if (userData != null) {
                                binding.userTxt.setText(userData.getName());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private void signOut() {
        binding.logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}