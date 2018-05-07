package com.android.example.paging.pagingwithnetwork.reddit.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import com.android.example.paging.pagingwithnetwork.GlideApp
import com.android.example.paging.pagingwithnetwork.R
import com.android.example.paging.pagingwithnetwork.reddit.model.RedditPost
import com.android.example.paging.pagingwithnetwork.reddit.repository.NetworkState
import kotlinx.android.synthetic.main.activity_reddit.*

class RedditActivity : AppCompatActivity() {
    companion object {
        const val KEY_SUBREDDIT = "subreddit"
        const val DEFAULT_SUBREDDIT = "androiddev"
        fun intentFor(context: Context): Intent {
            return Intent(context, RedditActivity::class.java)
        }
    }

    private lateinit var model: SubRedditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit)
        model = ViewModelProviders.of(this).get(SubRedditViewModel::class.java)
        initAdapter()
        initSwipeToRefresh()
        initSearch()
        val subreddit = savedInstanceState?.getString(KEY_SUBREDDIT) ?: DEFAULT_SUBREDDIT
        model.showSubreddit(subreddit)
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        val adapter = PostsAdapter(glide) {
            model.retry()
        }
        list.adapter = adapter
        model.posts.observe(this, Observer<PagedList<RedditPost>> {
            adapter.submitList(it)
        })
        model.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            model.refresh()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SUBREDDIT, model.currentSubreddit())
    }

    private fun initSearch() {
        input.setOnEditorActionListener({ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatedSubredditFromInput()
                true
            } else {
                false
            }
        })
        input.setOnKeyListener({ _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatedSubredditFromInput()
                true
            } else {
                false
            }
        })
    }

    private fun updatedSubredditFromInput() {
        input.text.trim().toString().let {
            if (it.isNotEmpty()) {
                if (model.showSubreddit(it)) {
                    list.scrollToPosition(0)
                    (list.adapter as? PostsAdapter)?.submitList(null)
                }
            }
        }
    }
}
