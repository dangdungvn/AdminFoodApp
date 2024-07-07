package com.example.foodappadmin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodappadmin.Activity.UserDetailActivity;
import com.example.foodappadmin.Domain.User;
import com.example.foodappadmin.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    ArrayList<User> items;
    Context context;
    public UserAdapter(ArrayList<User> items) {
        this.items = items;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_user, parent, false);
        return new UserAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        holder.email.setText(items.get(position).getEmail());
        holder.name.setText(items.get(position).getName());
        holder.sdt.setText(items.get(position).getSDT());
        holder.diaChi.setText(items.get(position).getDiaChi());
        holder.type.setText(items.get(position).getType()+"");
        Glide.with(context)
                .load(R.drawable.app_icon)
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra("Id", items.get(position).getId());
            context.startActivity(intent);
            ((Activity)context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView email, name, sdt, diaChi,type;
        ImageView pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.emailTxt);
            name = itemView.findViewById(R.id.nameTxt);
            sdt = itemView.findViewById(R.id.sdtTxt);
            diaChi = itemView.findViewById(R.id.diaChiTxt);
            pic = itemView.findViewById(R.id.pic);
            type = itemView.findViewById(R.id.typeTxt);
        }
    }
}
