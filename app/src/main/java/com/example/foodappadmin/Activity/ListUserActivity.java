package com.example.foodappadmin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodappadmin.Adapter.FoodsAdapter;
import com.example.foodappadmin.Adapter.UserAdapter;
import com.example.foodappadmin.Domain.User;
import com.example.foodappadmin.R;
import com.example.foodappadmin.databinding.ActivityListFoodsBinding;
import com.example.foodappadmin.databinding.ActivityListUserBinding;
import com.example.foodappadmin.databinding.ActivityUserDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListUserActivity extends BaseActivity  implements SwipeRefreshLayout.OnRefreshListener {
    ActivityListUserBinding binding;
    private RecyclerView.Adapter adapterListUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityListUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initList();
        setVariables();
    }

    private void setVariables() {
        binding.btnBack.setOnClickListener(view -> finish());
    }

    private void initList() {
        DatabaseReference myRef = database.getReference("User");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<User> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        list.add(user);
                    }
                    if(!list.isEmpty()){
                        binding.foodListView.setLayoutManager(new LinearLayoutManager(ListUserActivity.this, LinearLayoutManager.VERTICAL, false));
                        adapterListUser = new UserAdapter(list);
                        binding.foodListView.setAdapter(adapterListUser);
                        adapterListUser.notifyDataSetChanged();
                        binding.swipeRefreshLayout.setOnRefreshListener(ListUserActivity.this);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(ListUserActivity.this, AddUserActivity.class);
            intent.putExtra("UserId", -1);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onRefresh() {
        initList();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}