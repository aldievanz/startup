package com.example.startup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.tvNama.setText(review.getNama());
        holder.tvRating.setText("Rating: " + review.getRating()); // ini tambahan
        holder.tvUlasan.setText(review.getUlasan());
        holder.tvTanggal.setText(review.getTanggal());
        holder.ratingBar.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvRating, tvUlasan, tvTanggal;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.txtNamaPelanggan);
            tvRating = itemView.findViewById(R.id.txtRatingAngka);  // pastikan ini ada di XML
            tvUlasan = itemView.findViewById(R.id.txtUlasan);
            tvTanggal = itemView.findViewById(R.id.txtTanggal);
            ratingBar = itemView.findViewById(R.id.ratingBarReview);
        }
    }
}
