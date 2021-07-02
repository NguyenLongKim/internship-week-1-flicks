package com.example.flicks.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flicks.models.Item
import com.example.flicks.models.NowPlayingMovies
import com.example.flicks.models.Trailers
import com.example.flicks.repositories.MoviesRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class MainActivityViewModel : ViewModel() {
    private var items = ArrayList<Item>()
    private var itemsLiveData = MutableLiveData(items)
    private var isRefreshingLiveData = MutableLiveData<Boolean>()
    private var numOfLoadedPages = 0
    private var moviesRepo = MoviesRepository.instance

    init {
        loadMoreItems()
    }

    fun getItemsLiveData() = itemsLiveData

    fun getIsRefreshingLiveData() = isRefreshingLiveData

    fun loadMoreItems() {
        Log.d("VM", "Loading...")
        moviesRepo.loadItems(++numOfLoadedPages, object : Callback<NowPlayingMovies> {
            override fun onResponse(
                call: Call<NowPlayingMovies>,
                response: Response<NowPlayingMovies>
            ) {
                items.addAll(response.body()!!.results)
                itemsLiveData.value = items
                Log.d("VM", "Load successfully")
            }

            override fun onFailure(call: Call<NowPlayingMovies>, t: Throwable) {
                Log.d("VM", "Load failed")
            }

        })
    }

    fun getVideoKey(movie_id: Int, callBack: Callback<Trailers>) {
        moviesRepo.getVideoKey(movie_id, callBack)
    }

    fun refresh() {
        numOfLoadedPages = Random.nextInt(0, 10)
        items.clear()
        isRefreshingLiveData.value = true
        Log.d("VM", "Refreshing...")
        moviesRepo.loadItems(numOfLoadedPages, object : Callback<NowPlayingMovies> {
            override fun onResponse(
                call: Call<NowPlayingMovies>,
                response: Response<NowPlayingMovies>
            ) {
                items.addAll(response.body()!!.results)
                itemsLiveData.value = items
                isRefreshingLiveData.value = false
                Log.d("VM", "Refresh successfully")
            }

            override fun onFailure(call: Call<NowPlayingMovies>, t: Throwable) {
                Log.d("VM", "Refresh failed")
            }

        })
    }
}