package com.example.startup.pesanan;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.startup.R;
import com.example.startup.ui.dashboard.OrderItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import androidx.activity.result.ActivityResultLauncher;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.ViewHolder> {

    private Context context;
    private List<Pesanan> pesananList;
    private ActivityResultLauncher<Intent> uploadLauncher;
    private NumberFormat currencyFormat;

    public PesananAdapter(Context context, List<Pesanan> pesananList, ActivityResultLauncher<Intent> uploadLauncher) {
        this.context = context;
        this.pesananList = pesananList;
        this.uploadLauncher = uploadLauncher;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pesanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pesanan pesanan = pesananList.get(position);

        holder.textOrderNumber.setText(String.format("No. Order: %s - %s",
                pesanan.getOrderNumber(), pesanan.getTglOrder()));
        holder.textAlamat.setText("Alamat: " + pesanan.getAlamat());
        holder.textKota.setText("Kota: " + pesanan.getCityName());
        holder.textProvinsi.setText("Provinsi: " + pesanan.getProvinceName());
        holder.textOngkir.setText("Ongkir: " + currencyFormat.format(pesanan.getOngkir()));

        double subtotal = pesanan.getTotalBayar() - pesanan.getOngkir();
        holder.textSubTotal.setText("Subtotal: " + currencyFormat.format(subtotal));
        holder.textTotalBayar.setText("Total Bayar: " + currencyFormat.format(pesanan.getTotalBayar()));
        holder.textMetode.setText("Pembayaran: " + pesanan.getMetodePembayaran());
        holder.textKurir.setText("Kurir: " + pesanan.getKurir());
        holder.textLamaKirim.setText("Lama Kirim: " + pesanan.getLamaKirim());
        holder.textStatus.setText("Status: " + pesanan.getStatus());

        setStatusColor(holder.textStatus, pesanan.getStatus());
        setProductInfo(holder, pesanan.getItems());

        if (pesanan.getStatus().equalsIgnoreCase("Belum Dibayar")) {
            holder.layoutUpload.setVisibility(View.VISIBLE);
            holder.iconUploadBukti.setOnClickListener(v -> {
                Intent intent = new Intent(context, UploadBuktiActivity.class);
                intent.putExtra("order_number", pesanan.getOrderNumber());
                uploadLauncher.launch(intent);
            });
        } else {
            holder.layoutUpload.setVisibility(View.GONE);
        }
    }

    private void setProductInfo(ViewHolder holder, List<OrderItem> items) {
        if (items != null && !items.isEmpty()) {
            OrderItem firstItem = items.get(0);
            Glide.with(context)
                    .load("https://posyandukorowelangkulon.my.id/startup/Gambar_Product/" + firstItem.getFoto())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_orang)
                    .into(holder.imageProduct);

            StringBuilder productText = new StringBuilder("Produk:\n");
            for (OrderItem item : items) {
                productText.append(String.format("- %s x%d\n", item.getProduct_merk(), item.getQty()));
            }
            holder.textMerk.setText(productText.toString().trim());
        } else {
            holder.imageProduct.setImageResource(R.drawable.ic_orang);
            holder.textMerk.setText("Produk: Tidak ada produk");
        }
    }

    private void setStatusColor(TextView statusView, String status) {
        int color;
        switch (status.toLowerCase()) {
            case "selesai":
                color = context.getResources().getColor(R.color.green);
                break;
            case "dibatalkan":
                color = context.getResources().getColor(R.color.red);
                break;
            default:
                color = context.getResources().getColor(R.color.white);
                break;
        }
        statusView.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return pesananList != null ? pesananList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderNumber, textAlamat, textKota, textProvinsi,
                textOngkir, textSubTotal, textTotalBayar, textMetode,
                textKurir, textLamaKirim, textStatus, textMerk;
        ImageView iconUploadBukti, imageProduct;
        LinearLayout layoutUpload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderNumber = itemView.findViewById(R.id.textOrderNumber);
            textAlamat = itemView.findViewById(R.id.textAlamat);
            textKota = itemView.findViewById(R.id.textKota);
            textProvinsi = itemView.findViewById(R.id.textProvinsi);
            textOngkir = itemView.findViewById(R.id.textOngkir);
            textSubTotal = itemView.findViewById(R.id.textSubTotal);
            textTotalBayar = itemView.findViewById(R.id.textTotalBayar);
            textMetode = itemView.findViewById(R.id.textMetode);
            textKurir = itemView.findViewById(R.id.textKurir);
            textLamaKirim = itemView.findViewById(R.id.textLamaKirim);
            textStatus = itemView.findViewById(R.id.textStatus);
            textMerk = itemView.findViewById(R.id.textMerk);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            iconUploadBukti = itemView.findViewById(R.id.iconUploadBukti);
            layoutUpload = itemView.findViewById(R.id.layoutUpload);
        }
    }
}
