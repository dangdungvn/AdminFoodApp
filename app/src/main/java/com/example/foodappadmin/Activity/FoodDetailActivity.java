package com.example.foodappadmin.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodappadmin.Domain.Foods;
import com.example.foodappadmin.Domain.User;
import com.example.foodappadmin.R;
import com.example.foodappadmin.databinding.ActivityFoodDetailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FoodDetailActivity extends BaseActivity {
    ActivityFoodDetailBinding binding;
    private int Id;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        setVariables();
    }

    private void setVariables() {
        DatabaseReference userRef = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        Query query = userRef.orderByChild("Id").equalTo(Id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Foods foods = ds.getValue(Foods.class);
                        if (foods != null) {
                            binding.titleEdt.setText(foods.getTitle());
                            binding.priceEdt.setText(foods.getStringPrice());
                            if (foods.getLocationId() == 0) {
                                binding.locationEdt.setText("Hà Nội");
                            } else if (foods.getLocationId() == 1) {
                                binding.locationEdt.setText("Hồ Chí Minh");
                            }
                            if (foods.isBestFood()) {
                                binding.bestFoodChk.setChecked(true);
                            } else {
                                binding.bestFoodChk.setChecked(false);
                            }
                            if (foods.getCategoryId() == 0) {
                                binding.categoryEdt.setText("Pizza");
                            } else if (foods.getCategoryId() == 1) {
                                binding.categoryEdt.setText("Burger");
                            } else if (foods.getCategoryId() == 2) {
                                binding.categoryEdt.setText("Chicken");
                            } else if (foods.getCategoryId() == 3) {
                                binding.categoryEdt.setText("Sushi");
                            } else if (foods.getCategoryId() == 4) {
                                binding.categoryEdt.setText("Meat");
                            } else if (foods.getCategoryId() == 5) {
                                binding.categoryEdt.setText("Hotdog");
                            } else if (foods.getCategoryId() == 6) {
                                binding.categoryEdt.setText("Drink");
                            } else if (foods.getCategoryId() == 7) {
                                binding.categoryEdt.setText("More");
                            }
                            binding.timeEdt.setText(foods.getTimeValue() + "");
                            Glide.with(FoodDetailActivity.this)
                                    .load(foods.getImagePath())
                                    .transform(new CenterCrop(), new RoundedCorners(30))
                                    .into(binding.pic);
                            binding.descEdt.setText(foods.getDescription());
                            binding.ratingEdt.setText(foods.getStar() + "");
                        }
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.btnBack.setOnClickListener(v -> finish());
        binding.applyBtn.setOnClickListener(view -> {
            if(binding.titleEdt.getText().toString().isEmpty()){
                Toast.makeText(FoodDetailActivity.this, "Hãy nhập tên đồ ăn", Toast.LENGTH_SHORT).show();
                return;
            }
            if(binding.priceEdt.getText().toString().isEmpty()){
                Toast.makeText(FoodDetailActivity.this, "Hãy nhập giá tiền", Toast.LENGTH_SHORT).show();
                return;
            }
            if(binding.locationEdt.getText().toString().isEmpty()){
                Toast.makeText(FoodDetailActivity.this, "Hãy nhập địa điểm", Toast.LENGTH_SHORT).show();
                return;
            }
            if(binding.ratingEdt.getText().toString().isEmpty()){
                Toast.makeText(FoodDetailActivity.this, "Hãy nhập đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }
            userRef.child(String.valueOf(Id)).child("Title").setValue(binding.titleEdt.getText().toString());
            float price = Float.parseFloat(binding.priceEdt.getText().toString());
            userRef.child(String.valueOf(Id)).child("Price").setValue(Math.round(price * 100) / 100.0);
            userRef.child(String.valueOf(Id)).child("Star").setValue(Math.round(Float.parseFloat(binding.ratingEdt.getText().toString()) * 100) / 100.0);
            if (price < 10) {
                userRef.child(String.valueOf(Id)).child("PriceId").setValue(0);
            } else if ((price >= 10) && (price < 30)) {
                userRef.child(String.valueOf(Id)).child("PriceId").setValue(1);
            } else {
                userRef.child(String.valueOf(Id)).child("PriceId").setValue(2);
            }
            if (binding.locationEdt.getText().toString().equals("Hà Nội")) {
                userRef.child(String.valueOf(Id)).child("LocationId").setValue(0);
            } else if (binding.locationEdt.getText().toString().equals("Hồ Chí Minh")) {
                userRef.child(String.valueOf(Id)).child("LocationId").setValue(1);
            } else {
                binding.locationEdt.setError("Location not found");
            }
            userRef.child(String.valueOf(Id)).child("BestFood").setValue(binding.bestFoodChk.isChecked());
            if (binding.categoryEdt.getText().toString().equals("Pizza")) {
                userRef.child(String.valueOf(Id)).child("CategoryId").setValue(0);
            } else if (binding.categoryEdt.getText().toString().equals("Burger")) {
                userRef.child(String.valueOf(Id)).child("CategoryId").setValue(1);
            } else if (binding.categoryEdt.getText().toString().equals("Chicken")) {
                userRef.child(String.valueOf(Id)).child("CategoryId").setValue(2);
            } else if (binding.categoryEdt.getText().toString().equals("Sushi")) {
                userRef.child(String.valueOf(Id)).child("CategoryId").setValue(3);
            } else if (binding.categoryEdt.getText().toString().equals("Meat")) {
                userRef.child(String.valueOf(Id)).child("CategoryId").setValue(4);
            } else if (binding.categoryEdt.getText().toString().equals("Hotdog")) {
                userRef.child(String.valueOf(Id)).child("CategoryId").setValue(5);
            } else if (binding.categoryEdt.getText().toString().equals("Drink")) {
                userRef.child(String.valueOf(Id)).child("CategoryId").setValue(6);
            } else if (binding.categoryEdt.getText().toString().equals("More")) {
                userRef.child(String.valueOf(Id)).child("CategoryId").setValue(7);
            }
            int time = Integer.parseInt(binding.timeEdt.getText().toString());
            userRef.child(String.valueOf(Id)).child("TimeValue").setValue(time);
            if (time < 10) {
                userRef.child(String.valueOf(Id)).child("TimeId").setValue(0);
            } else if (time < 30) {
                userRef.child(String.valueOf(Id)).child("TimeId").setValue(1);
            } else {
                userRef.child(String.valueOf(Id)).child("TimeId").setValue(2);
            }

            if (imageUri != null) {
                StorageReference fileRef = storage.child("foods/" + Id + getFileExtension(imageUri));
                fileRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                                .addOnSuccessListener(uri ->
                                        userRef.child(String.valueOf(Id)).child("ImagePath").setValue(uri.toString())))
                                .addOnProgressListener(snapshot -> {

                        }).addOnFailureListener(e -> Toast.makeText(FoodDetailActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show());
            }
            userRef.child(String.valueOf(Id)).child("Description").setValue(binding.descEdt.getText().toString());
            finish();
        });
        binding.updatePic.setOnClickListener(view1 -> {
            Intent galaryIntent = new Intent();
            galaryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galaryIntent.setType("image/*");
            startActivityForResult(galaryIntent, 2);
        });
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .transform(new CenterCrop(), new RoundedCorners(30))
                    .into(binding.pic);
        }
    }


    private void getIntentExtra() {
        Id = getIntent().getIntExtra("FoodId", -1);
    }
}