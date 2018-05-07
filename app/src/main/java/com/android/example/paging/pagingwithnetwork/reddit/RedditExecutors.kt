package com.android.example.paging.pagingwithnetwork.reddit

import java.util.concurrent.Executors

object RedditExecutors {
    fun networkExecutor() = Executors.newFixedThreadPool(5)!!
}