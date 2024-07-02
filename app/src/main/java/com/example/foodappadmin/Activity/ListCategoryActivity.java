package com.example.foodappadmin.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodappadmin.Adapter.CategoryAdapter;
import com.example.foodappadmin.Domain.Category;
import com.example.foodappadmin.databinding.ActivityListCategoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListCategoryActivity extends BaseActivity {
    ActivityListCategoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityListCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleTxt2.setText("Category");

        DatabaseReference myRef = database.getReference("Category");

        binding.progressBar.setVisibility(View.VISIBLE);

        ArrayList<Category> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }
                    if (!list.isEmpty()) {
                        binding.foodListView.setLayoutManager(new LinearLayoutManager(ListCategoryActivity.this, LinearLayoutManager.VERTICAL, false));
                        RecyclerView.Adapter<CategoryAdapter.ViewHolder> adapter = new CategoryAdapter(list);
                        binding.foodListView.setAdapter(adapter);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}