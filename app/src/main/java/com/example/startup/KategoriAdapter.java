package com.example.startup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ViewHolder> {

    private Context context;
    private List<Kategori> kategoriList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Kategori kategori);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public KategoriAdapter(Context context, List<Kategori> kategoriList) {
        this.context = context;
        this.kategoriList = kategoriList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kategori, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Kategori kategori = kategoriList.get(position);
        holder.tvNamaKategori.setText(kategori.getNama());
        holder.imgKategori.setImageResource(kategori.getGambarResId());

        // Pasang listener klik di sini
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(kategori);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgKategori;
        TextView tvNamaKategori;

        public ViewHolder(View itemView) {
            super(itemView);
            imgKategori = itemView.findViewById(R.id.imgKategori);
            tvNamaKategori = itemView.findViewById(R.id.tvNamaKategori);
        }
    }
}
