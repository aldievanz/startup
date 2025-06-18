package com.example.startup.ui.dashboard;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.startup.R;

import java.util.List;

public class OrderConfirmationAdapter extends RecyclerView.Adapter<OrderConfirmationAdapter.ViewHolder> {

    private final List<OrderItem> orderItems;

    public OrderConfirmationAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_order_confirmation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        holder.tvMerk.setText(item.getMerk());
        holder.tvQuantity.setText("Qty: " + item.getQuantity());
        holder.tvHarga.setText(String.format("Rp %,.2f", item.getSubtotal()));


        // ✅ Load foto produk dengan Glide
        Glide.with(holder.itemView.getContext())
                .load("https://posyandukorowelangkulon.my.id/startup/Gambar_Product/" + item.getFoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.ivFoto);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMerk, tvQuantity, tvHarga;
        ImageView ivFoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMerk = itemView.findViewById(R.id.tvMerk);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            ivFoto = itemView.findViewById(R.id.ivFoto);
        }
    }
}
