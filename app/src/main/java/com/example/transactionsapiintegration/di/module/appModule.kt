package com.example.transactionsapiintegration.di.module

import android.content.Context
import android.util.Log
import com.example.transactionsapiintegration.di.data.ApiHelper
import com.example.transactionsapiintegration.di.data.ApiHelperImpl
import com.example.transactionsapiintegration.di.data.ApiService
import com.example.transactionsapiintegration.di.data.repository.MainRepository
import com.dictatenow.androidapp.utils.NetworkHelper
import com.example.transactionsapiintegration.utils.SharedPref
import com.example.transactionsapiintegration.BuildConfig
import com.example.transactionsapiintegration.utils.Constants
import com.example.transactionsapiintegration.view.viewModel.LoginViewModel
import com.example.transactionsapiintegration.view.viewModel.TransactionsViewModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val appModule = module {
    //sharedPref instance
    single { sharedPrefHelper(androidContext()) }
    //Network instance
    single { provideNetworkHelper(androidContext()) }

    single { provideOkHttpClient(get()) }
    single(named(BuildConfig.BASE_URL)) { provideRetrofit(get(), BuildConfig.BASE_URL) }
    single(named(BuildConfig.BASE_URL)) { provideApiService(get(named(BuildConfig.BASE_URL))) }
    single<ApiHelper>(named(BuildConfig.BASE_URL)) { ApiHelperImpl(get(named(BuildConfig.BASE_URL))) }

    //Repository instance
    single { MainRepository(get()) }

    //viewmodel
    single { LoginViewModel(get(),get()) }
    single { TransactionsViewModel(get(),get()) }




    // Add a definition for ApiHelper
    single<ApiHelper> { get(named(BuildConfig.BASE_URL)) }

}

private fun sharedPrefHelper(context: Context) = SharedPref(context)

private fun provideNetworkHelper(context: Context) = NetworkHelper(context)

private fun provideOkHttpClient(sharedPref: SharedPref) = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(
            chain: Array<out X509Certificate>?, authType: String?
        ) {
        }

        override fun checkServerTrusted(
            chain: Array<out X509Certificate>?, authType: String?
        ) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    loggingInterceptor.apply { loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY }
    OkHttpClient.Builder().addInterceptor(loggingInterceptor)
        .addInterceptor(TokenInterceptor(sharedPref))
        .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .callTimeout(300, TimeUnit.SECONDS)
        .connectTimeout(300, TimeUnit.SECONDS).readTimeout(300, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true).sslSocketFactory(SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory, trustAllCerts[0] as X509TrustManager).hostnameVerifier { _, _ -> true }
        .build()
} else OkHttpClient.Builder().addInterceptor(TokenInterceptor(sharedPref))
    .callTimeout(300, TimeUnit.SECONDS)
    .connectTimeout(300, TimeUnit.SECONDS).readTimeout(300, TimeUnit.SECONDS).build()


private fun provideRetrofit(
    okHttpClient: OkHttpClient, BASE_URL: String
): Retrofit =
    Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
        .client(okHttpClient).build()

private fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)


// TokenInterceptor Implementation
class TokenInterceptor(private val sharedPref: SharedPref) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val token = sharedPref.getString(Constants.TOKEN)
        if (token != "") {
            Log.d("tokenUser", token)
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
    }
}
