package com.example.foodappadmin.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodappadmin.Adapter.TimeAdapter;
import com.example.foodappadmin.Domain.Time;
import com.example.foodappadmin.R;
import com.example.foodappadmin.databinding.ActivityTimeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TimeActivity extends BaseActivity {
    ActivityTimeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> finish());
        initList();
    }

    private void initList() {
        binding.titleTxt2.setText("Giờ");
        binding.btnBack.setOnClickListener(v -> finish());
        binding.progressBar.setVisibility(View.VISIBLE);
        DatabaseReference myRef = database.getReference("Time");
        ArrayList<Time> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Time.class));
                    }
                    if (!list.isEmpty()) {
                        binding.foodListView.setLayoutManager(new LinearLayoutManager(TimeActivity.this, LinearLayoutManager.VERTICAL, false));
                        RecyclerView.Adapter<TimeAdapter.ViewHolder> adapter = new TimeAdapter(list);
                        binding.foodListView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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