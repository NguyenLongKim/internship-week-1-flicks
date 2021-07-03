package com.example.flicks

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flicks.adapters.MoviesAdapter
import com.example.flicks.databinding.ActivityMainBinding
import com.example.flicks.listeners.ItemClickListener
import com.example.flicks.models.Item
import com.example.flicks.models.Trailers
import com.example.flicks.viewmodels.MainActivityViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var moviesAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        mainActivityViewModel.getItemsLiveData()
            .observe(this, { moviesAdapter.notifyDataSetChanged() })
        initRecyclerView()
        mainActivityViewModel.getIsRefreshingLiveData().observe(this, { isRefreshing ->
            activityMainBinding.swipeContainer.isRefreshing = isRefreshing
        })
        activityMainBinding.swipeContainer.setOnRefreshListener { mainActivityViewModel.refresh() }
    }

    private fun initRecyclerView() {
        moviesAdapter = MoviesAdapter(mainActivityViewModel.getItemsLiveData().value!!)
        activityMainBinding.rvMovies.layoutManager = LinearLayoutManager(this)
        activityMainBinding.rvMovies.setHasFixedSize(true)
        activityMainBinding.rvMovies.adapter = moviesAdapter
        activityMainBinding.rvMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    mainActivityViewModel.loadMoreItems()
                }
            }
        })
        moviesAdapter.setItemClickListener(object : ItemClickListener {
            override fun onClick(item: Item) {
                val videoKey=mainActivityViewModel.getVideoKey(item.id)
                Toast.makeText(this@MainActivity, item.title, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, VideoActivity::class.java)
                intent.putExtra("item", item)
                intent.putExtra("video_key",videoKey )
                startActivity(intent)
            }
        })
    }

    override fun onBackPressed() {
        val layoutManager = activityMainBinding.rvMovies.layoutManager as LinearLayoutManager
        if ((layoutManager).findFirstVisibleItemPosition()>0){
            layoutManager.smoothScrollToPosition(activityMainBinding.rvMovies,null,0)
        }else{
            super.onBackPressed()
        }
    }
}