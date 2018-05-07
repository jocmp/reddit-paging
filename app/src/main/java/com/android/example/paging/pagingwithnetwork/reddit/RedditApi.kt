package com.android.example.paging.pagingwithnetwork.reddit

import android.util.Log
import com.android.example.paging.pagingwithnetwork.reddit.model.RedditPost
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditApi {
    @GET("/r/{subreddit}/hot.json")
    fun getTop(
            @Path("subreddit") subreddit: String,
            @Query("limit") limit: Int): Call<ListingResponse>

    // for after/before param, either get from RedditDataResponse.after/before,
    // or pass RedditNewsDataResponse.name (though this is technically incorrect)
    @GET("/r/{subreddit}/hot.json")
    fun getTopAfter(
            @Path("subreddit") subreddit: String,
            @Query("after") after: String,
            @Query("limit") limit: Int): Call<ListingResponse>

    class ListingResponse(val data: ListingData)

    class ListingData(
            val children: List<RedditChildrenResponse>,
            val before: String?,
            val after: String?
    )

    data class RedditChildrenResponse(val data: RedditPost)

    companion object {
        private const val BASE_URL = "https://www.reddit.com/"
        val instance: RedditApi by lazy {
            val httpUrl = HttpUrl.parse(BASE_URL)!!
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC
            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
            Retrofit.Builder()
                    .baseUrl(httpUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RedditApi::class.java)
        }
    }
}