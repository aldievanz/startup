package com.example.startup;




import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class KontakKamiFragment extends Fragment {

    private TextView tvAlamat, tvKontak;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_kontak_kami, container, false);

        // Inisialisasi tampilan
        tvAlamat = root.findViewById(R.id.tvAlamat);
        tvKontak = root.findViewById(R.id.tvKontak);

        // Isi informasi kontak
        tvAlamat.setText("Citraland Mall, Jalan simpang lima 1 Mal Ciputra, East Extention No.VII lantai 1, Pekunden, Jawa Tengah 50124");
        tvKontak.setText("Telepon: (+62)89669923632 \nEmail: Oneda@gmail.com");

        // Listener untuk buka Google Maps
        tvAlamat.setOnClickListener(v -> {
            String lokasi = "https://www.google.com/maps/search/?api=1&query=Citraland+Mall,+Jalan+Simpang+Lima+1+Mal+Ciputra,+East+Extention+No.VII+lantai+1,+Pekunden,+Jawa+Tengah+50124";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lokasi));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });

        // Listener untuk WhatsApp
        tvKontak.setOnClickListener(v -> {
            String phone = "6289669923632";
            String url = "https://wa.me/" + phone;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        return root;
    }
}
