package com.example.startup.ui.dashboard;

public class ResponsePesanan {
    private boolean success;
    private String message;
    private String error;
    private String snapToken; // optional
    private String order_number; // tambahan untuk nomor pesanan

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getSnapToken() {
        return snapToken; // ✅ PERBAIKAN: return snapToken, bukan error
    }

    public String getOrderNumber() {
        return order_number;
    }

    // Setters (opsional, tapi good practice)
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setSnapToken(String snapToken) {
        this.snapToken = snapToken;
    }

    public void setOrderNumber(String orderNumber) {
        this.order_number = orderNumber;
    }

    @Override
    public String toString() {
        return "ResponsePesanan{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                ", snapToken='" + snapToken + '\'' +
                ", order_number='" + order_number + '\'' +
                '}';
    }

}
