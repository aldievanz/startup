package com.example.startup.ui.product;

import java.io.Serializable;

public class Product implements Serializable {
    private String kode;
    private String merk;
    private double hargajual;
    private int stok;
    private String foto;
    private String deskripsi;
    private String kategori;
    private int view; // Tambahan: jumlah view produk

    // Konstruktor
    public Product(String kode, String merk, double hargajual, int stok, String foto, String deskripsi, String kategori, int view) {
        this.kode = kode;
        this.merk = merk;
        this.hargajual = hargajual;
        this.stok = stok;
        this.foto = foto;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.view = view;
    }


    // Getter & Setter
    public String getKode() {
        return kode;
    }

    public String getMerk() {
        return merk;
    }

    public double getHargajual() {
        return hargajual;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getFoto() {
        return foto;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getKategori() {
        return kategori;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }
}
