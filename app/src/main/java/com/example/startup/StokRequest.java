package com.example.startup;

public class StokRequest {
    private String kode;
    private int stok;

    public StokRequest(String kode, int stok) {
        this.kode = kode;
        this.stok = stok;
    }

    public String getKode() {
        return kode;
    }

    public int getStok() {
        return stok;
    }
}
