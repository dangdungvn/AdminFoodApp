package com.example.foodappadmin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodappadmin.Activity.BaseActivity;
import com.example.foodappadmin.Adapter.FoodsAdapter;
import com.example.foodappadmin.databinding.ActivityListFoodsBinding;

import com.example.foodappadmin.Domain.Foods;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListFoodsActivity extends BaseActivity implements  SwipeRefreshLayout.OnRefreshListener {
    ActivityListFoodsBinding binding;
    private RecyclerView.Adapter adapterListFood;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;
    private SwipeRefreshLayout refreshLayout;


    public ListFoodsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityListFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        initList();
        setVariables();
    }

    private void setVariables() {
        binding.btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(ListFoodsActivity.this, AddFoodActivity.class);
            intent.putExtra("FoodId", -1);
            startActivity(intent);
            finish();
        });
    }

    private void initList() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query;
        if(isSearch){
            query = myRef.orderByChild("Title").startAt(searchText).endAt(searchText + "\uf8ff");
        }else if(categoryId != -1){
            query = myRef.orderByChild("CategoryId").equalTo(categoryId);
        } else {
            query = myRef;
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot issue : snapshot.getChildren()){
                        list.add(issue.getValue(Foods.class));
                    }
                    if(!list.isEmpty()){
                        binding.foodListView.setLayoutManager(new LinearLayoutManager(ListFoodsActivity.this, LinearLayoutManager.VERTICAL, false));
                        adapterListFood = new FoodsAdapter(list);
                        binding.foodListView.setAdapter(adapterListFood);
                        adapterListFood.notifyDataSetChanged();
                        binding.swipeRefreshLayout.setOnRefreshListener(ListFoodsActivity.this);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", -1);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);
        if(isSearch){
            binding.titleTxt2.setText(searchText);
        }else if(categoryId != -1){
            binding.titleTxt2.setText(categoryName);
        } else if (categoryId == -1) {
            binding.titleTxt2.setText("Đồ ăn");
        }
        binding.btnBack.setOnClickListener(v -> finish());
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