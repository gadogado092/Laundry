package amat.kelolakost.ui.screen.back_up

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()



    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://apix.juragankost.id/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    val backUpService: BackUpService by lazy {
        retrofit.create(BackUpService::class.java)
    }

}