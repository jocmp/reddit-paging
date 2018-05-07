package com.android.example.paging.pagingwithnetwork.reddit.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "posts",
        indices = [Index(value = ["subreddit"], unique = false)])
data class RedditPost(
        @PrimaryKey
        @SerializedName("name")
        val name: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("score")
        val score: Int,
        @SerializedName("author")
        val author: String,
        @SerializedName("subreddit") // this seems mutable but fine for a demo
        @ColumnInfo(collate = ColumnInfo.NOCASE)
        val subreddit: String,
        @SerializedName("num_comments")
        val num_comments: Int,
        @SerializedName("created_utc")
        val created: Long,
        val thumbnail: String?,
        val url: String?) {
    // to be consistent w/ changing backend order, we need to keep a data like this
    var indexInResponse: Int = -1
}