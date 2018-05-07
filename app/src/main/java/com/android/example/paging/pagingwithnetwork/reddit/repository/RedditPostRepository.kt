package com.android.example.paging.pagingwithnetwork.reddit.repository

import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.support.annotation.MainThread
import com.android.example.paging.pagingwithnetwork.reddit.RedditApi
import com.android.example.paging.pagingwithnetwork.reddit.RedditExecutors
import com.android.example.paging.pagingwithnetwork.reddit.model.RedditPost
import java.util.concurrent.Executor

class RedditPostRepository(private val redditApi: RedditApi = RedditApi.instance,
                           private val networkExecutor: Executor = RedditExecutors.networkExecutor()) {
    @MainThread
    fun postsOfSubreddit(subReddit: String, pageSize: Int): Listing<RedditPost> {
        val sourceFactory = SubRedditDataSourceFactory(redditApi, subReddit, networkExecutor)
        val livePagedList = LivePagedListBuilder(sourceFactory, pageSize).setFetchExecutor(networkExecutor).build()
        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) { it.initialLoad }
        return Listing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveData, { it.networkState }),
                retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() },
                refresh = { sourceFactory.sourceLiveData.value?.invalidate() },
                refreshState = refreshState
        )
    }

    companion object {
        val instance: RedditPostRepository by lazy {
            RedditPostRepository()
        }
    }
}

