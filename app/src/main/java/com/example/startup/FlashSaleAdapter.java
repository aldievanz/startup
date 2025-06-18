package com.example.startup;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.startup.fs.Product;
import com.example.startup.ui.dashboard.OrderHelper;

import java.util.List;

public class FlashSaleAdapter extends RecyclerView.Adapter<FlashSaleAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public FlashSaleAdapter(Context context, List<Product> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FlashSaleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flash_sale, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashSaleAdapter.ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(context, product, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProductImage;
        TextView tvProductName, tvDiscountPrice, tvOriginalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvDiscountPrice = itemView.findViewById(R.id.tvDiscountPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
        }

        public void bind(Context context, Product product, OnItemClickListener listener) {
            tvProductName.setText(product.getMerk());

            float hargaJual = (float) product.getHargaJual();
            float hargaDiskon = hargaJual - (hargaJual * 0.4f);

            tvDiscountPrice.setText(String.format("Rp %,d", (int) hargaDiskon));
            tvOriginalPrice.setText(String.format("Rp %,d", (int) hargaJual));
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            Glide.with(context)
                    .load("https://posyandukorowelangkulon.my.id/android/Gambar_Product/" + product.getFoto())
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .error(android.R.drawable.stat_notify_error)
                    .into(ivProductImage);

            itemView.setOnClickListener(v -> {
                if (product.getStok() <= 0) {
                    Toast.makeText(context, "Stok habis!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ambil email login dari SharedPreferences user
                SharedPreferences userPrefs = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                String email = userPrefs.getString("email", "guest@example.com");

                // Buat OrderHelper dengan key berdasarkan email
                OrderHelper orderHelper = new OrderHelper(context, email);

                // Convert product jika perlu
                com.example.startup.ui.product.Product convertedProduct = convertToProduct(product);

                // Tambahkan ke order
                orderHelper.addToOrder(convertedProduct, "M");
                Toast.makeText(context, product.getMerk() + " ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();

            });

        }

        private com.example.startup.ui.product.Product convertToProduct(Product fsProduct) {
            float hargaJual = (float) fsProduct.getHargaJual();
            float hargaDiskon = hargaJual - (hargaJual * 0.4f);

            return new com.example.startup.ui.product.Product(
                    fsProduct.getKode(),
                    fsProduct.getMerk(),
                    hargaDiskon,
                    0,
                    fsProduct.getFoto(),
                    "",
                    "",
                    fsProduct.getStok() // penting untuk menyimpan stok
            );
        }
    }
}
