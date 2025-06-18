package com.example.startup;

public class Kategori {
    private String nama;
    private int gambarResId;

    public Kategori(String nama, int gambarResId) {
        this.nama = nama;
        this.gambarResId = gambarResId;
    }

    public String getNama() {
        return nama;
    }

    public int getGambarResId() {
        return gambarResId;
    }
}
