package com.example.startup.fs;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Product implements Serializable {

    @SerializedName("kode")
    private String kode;

    @SerializedName("merk")
    private String merk;

    @SerializedName("hargajual")
    private double hargaJual;

    @SerializedName("diskonjual")
    private double hargaDiskon;

    @SerializedName("stok")
    private int stok;

    @SerializedName("foto")
    private String foto;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("kategori")
    private String kategori;

    @SerializedName("view")
    private int view;

    public Product(String kode, String merk, double hargaJual, double hargaDiskon, int stok, String foto, String deskripsi, String kategori, int view) {
        this.kode = kode;
        this.merk = merk;
        this.hargaJual = hargaJual;
        this.hargaDiskon = hargaDiskon;
        this.stok = stok;
        this.foto = foto;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.view = view;
    }

    public String getKode() {
        return kode;
    }

    public String getMerk() {
        return merk;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public double getHargaDiskon() {
        return hargaDiskon;
    }

    public int getStok() {
        return stok;
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
}
