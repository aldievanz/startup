package com.example.startup;


import com.google.gson.annotations.SerializedName;

public class ResponsePromo {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private PromoData data;

    // Constructor
    public ResponsePromo() {}

    public ResponsePromo(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PromoData getData() {
        return data;
    }

    public void setData(PromoData data) {
        this.data = data;
    }

    // Method untuk mendapatkan nilai diskon
    public double getNilaiDiskon() {
        if (data != null) {
            return data.getNilaiDiskon();
        }
        return 0.0;
    }

    // Method untuk mengecek apakah promo berupa persentase
    public boolean isPersenDiskon() {
        if (data != null) {
            return "persen".equals(data.getTipeDiskon());
        }
        return false;
    }

    // Method untuk mengecek apakah promo berupa nominal
    public boolean isNominalDiskon() {
        if (data != null) {
            return "nominal".equals(data.getTipeDiskon());
        }
        return false;
    }

    // Inner class untuk data promo
    public static class PromoData {
        @SerializedName("kode_promo")
        private String kodePromo;

        @SerializedName("tipe_diskon")
        private String tipeDiskon; // akan berisi "persen" atau "nominal"

        @SerializedName("nilai_diskon_persen")
        private double nilaiDiskonPersen;

        @SerializedName("nilai_diskon")
        private double nilaiDiskon;

        @SerializedName("minimal_belanja")
        private double minimalBelanja; // dari minimal_pembelian di database

        @SerializedName("maksimal_diskon")
        private Double maksimalDiskon; // bisa null

        // Constructor
        public PromoData() {}

        // Getters and Setters
        public String getKodePromo() {
            return kodePromo != null ? kodePromo : "";
        }

        public void setKodePromo(String kodePromo) {
            this.kodePromo = kodePromo;
        }

        public String getTipeDiskon() {
            return tipeDiskon != null ? tipeDiskon : "";
        }

        public void setTipeDiskon(String tipeDiskon) {
            this.tipeDiskon = tipeDiskon;
        }

        public double getNilaiDiskonPersen() {
            return nilaiDiskonPersen;
        }

        public void setNilaiDiskonPersen(double nilaiDiskonPersen) {
            this.nilaiDiskonPersen = nilaiDiskonPersen;
        }

        public double getNilaiDiskon() {
            return nilaiDiskon;
        }

        public void setNilaiDiskon(double nilaiDiskon) {
            this.nilaiDiskon = nilaiDiskon;
        }

        public double getMinimalBelanja() {
            return minimalBelanja;
        }

        public void setMinimalBelanja(double minimalBelanja) {
            this.minimalBelanja = minimalBelanja;
        }

        public Double getMaksimalDiskon() {
            return maksimalDiskon;
        }

        public void setMaksimalDiskon(Double maksimalDiskon) {
            this.maksimalDiskon = maksimalDiskon;
        }

        // Helper method untuk mendapatkan maksimal diskon dengan safety check
        public double getMaksimalDiskonValue() {
            return maksimalDiskon != null ? maksimalDiskon : 0.0;
        }

        // Helper method untuk mengecek apakah ada batasan maksimal diskon
        public boolean hasMaksimalDiskon() {
            return maksimalDiskon != null && maksimalDiskon > 0;
        }

        // Method untuk mendapatkan text deskripsi diskon
        public String getDiskonDescription() {
            if ("persen".equals(tipeDiskon)) {
                if (hasMaksimalDiskon()) {
                    return String.format("%.0f%% (Maks. Rp %.0f)", nilaiDiskonPersen, getMaksimalDiskonValue());
                } else {
                    return String.format("%.0f%%", nilaiDiskonPersen);
                }
            } else if ("nominal".equals(tipeDiskon)) {
                return String.format("Rp %.0f", nilaiDiskonPersen);
            }
            return "";
        }

        // Method untuk mendapatkan text minimal belanja
        public String getMinimalBelanjaText() {
            if (minimalBelanja > 0) {
                return String.format("Min. belanja Rp %.0f", minimalBelanja);
            }
            return "Tanpa minimal belanja";
        }
    }
}