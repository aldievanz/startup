package com.example.startup;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.startup.ui.product.Product;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private final List<Product> wishlist;

    public WishlistAdapter(List<Product> wishlist) {
        this.wishlist = wishlist;
    }

    @NonNull
    @Override
    public WishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wishlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.ViewHolder holder, int position) {
        Product item = wishlist.get(position);

        holder.tvMerk.setText(item.getMerk());
        holder.tvHarga.setText("Rp " + item.getHargajual());
        holder.tvStok.setText("Stok: " + item.getStok());

        Glide.with(holder.itemView.getContext())
                .load("https://posyandukorowelangkulon.my.id/startup/Gambar_Product/" + item.getFoto())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.imageProduct);
        holder.btnHapusWishlist.setOnClickListener(v -> {
            // Hapus dari SharedPreferences
            SharedPreferences prefs = holder.itemView.getContext()
                    .getSharedPreferences("wishlist", Context.MODE_PRIVATE);
            prefs.edit().remove("wishlist_" + item.getKode()).apply();

            // Hapus dari list adapter dan update UI
            wishlist.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, wishlist.size());

            Toast.makeText(holder.itemView.getContext(),
                    "Dihapus dari wishlist", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMerk, tvHarga, tvStok;
        ImageView imageProduct;
        ImageButton btnHapusWishlist;

        public ViewHolder(View view) {
            super(view);
            tvMerk = view.findViewById(R.id.tvMerk);
            tvHarga = view.findViewById(R.id.tvHarga);
            tvStok = view.findViewById(R.id.tvStok);
            imageProduct = view.findViewById(R.id.imageProduct);
            btnHapusWishlist = view.findViewById(R.id.btnHapusWishlist);
        }
    }
}
