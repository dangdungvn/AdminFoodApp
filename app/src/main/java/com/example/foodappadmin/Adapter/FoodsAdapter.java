package com.example.foodappadmin.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodappadmin.Activity.FoodDetailActivity;
import com.example.foodappadmin.Domain.Foods;

import com.example.foodappadmin.Domain.Location;
import com.example.foodappadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoodsAdapter extends RecyclerView.Adapter<FoodsAdapter.ViewHolder> {
    ArrayList<Foods> list;
    Context context;

    public FoodsAdapter(ArrayList<Foods> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_food, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Location");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Location loc = ds.getValue(Location.class);
                        if (loc.getId() == list.get(position).getLocationId()) {
                            holder.totalEachItem.setText(loc.getLoc());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.title.setText(list.get(position).getTitle());
        holder.feeEachItem.setText("$" + list.get(position).getPrice());
        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);
        holder.descItemTxt.setText(list.get(position).getDescription());
        holder.timeItem.setText("" + list.get(position).getTimeValue() + " min");
        if (list.get(position).isBestFood()) {
            holder.bestFoodItem.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else {
            holder.bestFoodItem.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.purple));
        }
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), FoodDetailActivity.class);
            intent.putExtra("FoodId", list.get(position).getId());
            holder.itemView.getContext().startActivity(intent);
            notifyItemChanged(position);
            ((Activity)context).finish();
        });
        holder.itemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete")
                    .setMessage("Bạn có chắc muốn xóa không ?")
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        DatabaseReference userRef = database.getReference("Foods");
                        userRef.child(String.valueOf(list.get(position).getId())).removeValue();
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> {
                    }).show();
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, feeEachItem, descItemTxt, totalEachItem, timeItem;
        ImageView pic;
        CardView bestFoodItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.locationTxt);
            pic = itemView.findViewById(R.id.pic);
            descItemTxt = itemView.findViewById(R.id.descTxt);
            timeItem = itemView.findViewById(R.id.timeTxt);
            bestFoodItem = itemView.findViewById(R.id.foodItem);
        }
    }
}
