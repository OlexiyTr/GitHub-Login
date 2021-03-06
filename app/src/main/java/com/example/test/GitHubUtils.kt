package com.example.test

import android.net.Uri
import com.example.test.data.AccessToken
import com.example.test.data.GitHubService
import com.example.test.data.User
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit

@ExperimentalSerializationApi
class GitHubUtils {

    private companion object {
        const val clientId = "Iv1.9adf0bf789a874bc"
        const val clientSecret = "c3689fa836cbfde5a0d67a88278d6e8a01412fdd"
        const val redirectUrl = "parimatchacademy://callback"
        const val scopes = "repo, user"
        const val schema = "https"
        const val host = "github.com"
    }

    private val converterFactory: Converter.Factory by lazy {
        Json { ignoreUnknownKeys = true }
            .asConverterFactory("application/json".toMediaType())
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(
                OkHttpClient().newBuilder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            )
            .baseUrl(HttpUrl.Builder().scheme(schema).host(host).build())
            .addConverterFactory(converterFactory)
            .build()
    }

    private val githubService: GitHubService by lazy {
        retrofit.create(GitHubService::class.java)
    }

    fun buildAuthGitHubUrl(): Uri {
        return Uri.Builder()
            .scheme(schema)
            .authority(host)
            .appendEncodedPath("login/oauth/authorize")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("scope", scopes)
            .appendQueryParameter("redirect_url", redirectUrl)
            .build()
    }

    fun getCodeFromUri(uri: Uri?): String? {
        uri ?: return null
        if (!uri.toString().startsWith(redirectUrl)) {
            return null
        }
        return uri.getQueryParameter("code")
    }

    suspend fun getAccesToken(code: String): AccessToken {
        return githubService.getAccessToken(clientId, clientSecret, code)
    }

    suspend fun getUser(token: String): User {
        return githubService.getUser(token)
    }
}