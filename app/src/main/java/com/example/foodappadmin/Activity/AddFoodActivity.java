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


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import com.example.foodappadmin.databinding.ActivityAddFoodBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class AddFoodActivity extends BaseActivity {

    ActivityAddFoodBinding binding;
    int Id;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        setVariables();
        onClick();

    }

    private void onClick() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.updatePic.setOnClickListener(view -> {
            Intent galaryIntent = new Intent();
            galaryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galaryIntent.setType("image/*");
            startActivityForResult(galaryIntent, 3);
        });
        binding.applyBtn.setOnClickListener(view -> {
            if(binding.titleEdt.getText().toString().isEmpty()){
                binding.titleEdt.setError("Vui lòng nhập tên");
                return;
            }
            if(binding.priceEdt.getText().toString().isEmpty()){
                binding.priceEdt.setError("Vui lòng nhập giá");
                return;
            }
            if(binding.ratingEdt.getText().toString().isEmpty()){
                binding.ratingEdt.setError("Vui lòng nhập đánh giá");
                return;
            }
            if(binding.categoryEdt.getText().toString().isEmpty()){
                binding.categoryEdt.setError("Vui lòng nhập loại");
                return;
            }
            if(binding.locationEdt.getText().toString().isEmpty()){
                binding.locationEdt.setError("Vui lòng nhập địa điểm");
                return;
            }
            if(binding.descEdt.getText().toString().isEmpty()){
                binding.descEdt.setError("Vui lòng nhập mô tả");
                return;
            }
            DatabaseReference userRef = database.getReference("Foods");
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
                    }else {
                        Id = 0;
                    }
                    userRef.child(String.valueOf(Id)).child("Id").setValue(Id);
                    userRef.child(String.valueOf(Id)).child("Title").setValue(binding.titleEdt.getText().toString());
                    userRef.child(String.valueOf(Id)).child("Star").setValue(Math.round(Float.parseFloat(binding.ratingEdt.getText().toString()) * 100) / 100.0);
                    float price = Float.parseFloat(binding.priceEdt.getText().toString());
                    userRef.child(String.valueOf(Id)).child("Price").setValue(Math.round(price * 100) / 100.0);
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
                    } else if ((time >= 10) && (time < 30)) {
                        userRef.child(String.valueOf(Id)).child("TimeId").setValue(1);
                    } else {
                        userRef.child(String.valueOf(Id)).child("TimeId").setValue(2);
                    }

                    if (imageUri != null) {
                        StorageReference fileRef = storage.child("foods/" + Id + getFileExtension(imageUri));
                        fileRef.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                                        .addOnSuccessListener(uri ->
                                                userRef.child(String.valueOf(Id)).child("ImagePath").setValue(uri.toString()))).
                                addOnProgressListener(snapshot1 -> {

                                }).addOnFailureListener(e -> Toast.makeText(AddFoodActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show());
                    }
                    userRef.child(String.valueOf(Id)).child("Description").setValue(binding.descEdt.getText().toString());
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });
    }

    private void setVariables() {
        binding.titleEdt.setText("");
        binding.priceEdt.setText("");
        binding.bestFoodChk.setChecked(false);
        binding.categoryEdt.setText("");
        binding.locationEdt.setText("");
        binding.descEdt.setText("");
        binding.ratingEdt.setText("");
        binding.pic.setImageURI(null);
        binding.progressBar.setVisibility(View.GONE);

    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        Id = intent.getIntExtra("FoodId", -1);
    }
    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .transform(new CenterCrop(), new RoundedCorners(30))
                    .into(binding.pic);
        }
    }
}