package com.example.startup.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.startup.DetailProductActivity;
import com.example.startup.R;
import com.example.startup.ui.product.Product;

import java.util.List;

public class RekomendasiAdapter extends RecyclerView.Adapter<RekomendasiAdapter.ViewHolder> {

    private Context context;
    private List<Product> rekomendasiList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(Product product);
    }

    public RekomendasiAdapter(Context context, List<Product> rekomendasiList, OnItemClickListener listener) {
        this.context = context;
        this.rekomendasiList = rekomendasiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = rekomendasiList.get(position);
        holder.tvMerk.setText(product.getMerk());
        holder.tvHarga.setText("Rp " + product.getHargajual());
        holder.tvStok.setText(product.getStok() > 0 ? "Stok Tersedia" : "Stok Habis");
        holder.tvViewCount.setText("Dilihat: " + product.getView() + "x");

        // Load gambar
        Glide.with(context)
                .load("https://posyandukorowelangkulon.my.id/startup/Gambar_Product/" + product.getFoto())
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.imageProduct);
        holder.btnKeranjang.setOnClickListener(v -> listener.onClick(product));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailProductActivity.class);
            intent.putExtra("produk", product); // Kirim seluruh objek
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return rekomendasiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView tvMerk, tvHarga, tvStok, tvViewCount, tvDeskripsi;
        ImageButton btnKeranjang;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            tvMerk = itemView.findViewById(R.id.tvMerk);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvDeskripsi = itemView.findViewById(R.id.tvDetailDeskripsi);
            tvStok = itemView.findViewById(R.id.tvStok);
            tvViewCount = itemView.findViewById(R.id.tvViewCount);
            btnKeranjang = itemView.findViewById(R.id.btnKeranjang);
        }
    }
    public void updateList(List<Product> newList) {
        this.rekomendasiList = newList;
        notifyDataSetChanged();
    }


}
