package com.example.startup;

import com.example.startup.pesanan.Order;
import com.example.startup.ui.dashboard.ResponsePesanan;
import com.example.startup.ui.product.Product;
import com.example.startup.ui.product.ViewResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("post_login.php")
    Call<ResponseBody> login(@Field("email") String email,    // disamakan dengan field di PHP
                             @Field("password") String password);
    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> register(
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("password") String password
    );


    @GET("get_profile.php")
    Call<List<UserProfile>> getProfile(@Query("email") String email);




    @Multipart
    @POST("update_profile.php")
    Call<ResponseBody> updateProfile(
            @Part("email") RequestBody email,
            @Part("nama") RequestBody nama,
            @Part("alamat") RequestBody alamat,
            @Part("kota") RequestBody kota,
            @Part("provinsi") RequestBody provinsi,
            @Part("telepon") RequestBody telepon,
            @Part("kodepos") RequestBody kodepos,
            @Part MultipartBody.Part foto  // Bisa dikirim null jika tidak ingin update foto
    );


    @FormUrlEncoded
    @POST("api_update_stok.php")
    Call<Void> updateStok(
            @Field("kode") String kode,
            @Field("stok") int stok
    );
    @FormUrlEncoded
    @POST("updateView.php")
    Call<ViewResponse> updateView(@Field("kode") String kode);
    @FormUrlEncoded
    @POST("insert_view.php")  // URL untuk file PHP kamu
    Call<ViewResponse> tambahView(@Field("kode") String kode);
    @GET("kategori")
    Call<List<Kategori>> getKategori();
    @Multipart
    @POST("uploadimage.php")
    Call<ResponseUpload> uploadImage(@Part MultipartBody.Part image);
    @GET("get_all_products.php")
    Call<List<Product>> getAllProducts();
    @GET("product_flashsale.php")
    Call<List<com.example.startup.fs.Product>> getFlashSaleProducts();

    @GET("get_product_images.php")
    Call<List<String>> getProductImages(@Query("kode") String kode);
    @FormUrlEncoded
    @POST("simpan_pesanan.php")
    Call<ResponsePesanan> simpanPesanan(
            @Field("email") String email,
            @Field("nama_pelanggan") String namaPelanggan,
            @Field("telp_pelanggan") String telpPelanggan,
            @Field("alamat") String alamat,
            @Field("kodepos_pelanggan") String kodeposPelanggan,
            @Field("province_id") String provinceId,
            @Field("province_name") String provinceName,
            @Field("city_id") String cityId,
            @Field("city_name") String cityName,
            @Field("kurir") String kurir,
            @Field("ongkir") double ongkir,
            @Field("total_bayar") double totalBayar,
            @Field("metode_pembayaran") String metodePembayaran,
            @Field("lama_kirim") String lamaKirim,
            @Field("order_items") String orderItemsJson,
            @Field("kode_promo") String kodePromo,      // Baru
            @Field("diskon_promo") double diskonPromo
    );
    @FormUrlEncoded
    @POST("validasi_promo.php")
    Call<ResponsePromo> validasiPromo(
            @Field("kode_promo") String kodePromo,
            @Field("total_belanja") double totalBelanja
    );

    @GET("get_size.php")
    Call<List<UkuranStok>> getUkuranStok(@Query("kode") String kodeProduk);

    @Multipart
    @POST("upload_bukti.php")
    Call<ResponseBody> uploadBukti(
            @Part("order_number") RequestBody orderNumber,
            @Part MultipartBody.Part bukti
    );
    @GET("get_orders.php")
    Call<List<Order>> getOrders(@Query("email") String email);



}





