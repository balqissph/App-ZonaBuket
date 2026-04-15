package com.ppl3.appzonabuket.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // Sesuai dengan rute yang kita buat di routes/api.php Laravel
    @Headers("Accept: application/json")
    @POST("payment/snap-token")
    fun getSnapToken(@Body request: CheckoutRequest): Call<CheckoutResponse>

    // Fungsi baru untuk mengecek status
    @Headers("Accept: application/json")
    @GET("payment/status/{order_id}")
    fun checkPaymentStatus(@Path("order_id") orderId: String): Call<StatusResponse>
}