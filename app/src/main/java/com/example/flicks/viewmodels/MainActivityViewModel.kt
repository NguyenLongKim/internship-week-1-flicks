package com.example.flicks.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flicks.models.Item
import com.example.flicks.models.NowPlayingMovies
import com.example.flicks.models.PopularMovies
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
    private var pageCount = 0
    private var moviesRepo = MoviesRepository.instance
    private var trailersMap = mutableMapOf<Int, Trailers?>()

    init {
        loadMoreItems()
    }

    fun getItemsLiveData() = itemsLiveData

    fun getIsRefreshingLiveData() = isRefreshingLiveData

    fun loadMoreItems() {
        Log.d("VM", "Loading movies...")
        moviesRepo.loadItems(++pageCount, object : Callback<NowPlayingMovies> {
            override fun onResponse(
                call: Call<NowPlayingMovies>,
                response: Response<NowPlayingMovies>
            ) {
                if (response.body() != null) {
                    val newItems = response.body()!!.results
                    items.addAll(newItems)
                    itemsLiveData.value = items
                    loadTrailers(newItems)
                    Log.d("VM", "Load movies successfully")
                }
            }

            override fun onFailure(call: Call<NowPlayingMovies>, t: Throwable) {
                Log.d("VM", "Load movies failed")
            }

        })
    }


    fun refresh() {
        pageCount = 0
        items.clear()
        trailersMap.clear()
        isRefreshingLiveData.value = true
        Log.d("VM", "Refreshing...")
        moviesRepo.loadPopularItems(Random.nextInt(1, 10), object : Callback<PopularMovies> {
            override fun onResponse(call: Call<PopularMovies>, response: Response<PopularMovies>) {
                if (response.body() != null) {
                    val newItems = response.body()!!.results
                    items.addAll(newItems)
                    itemsLiveData.value = items
                    isRefreshingLiveData.value = false
                    loadTrailers(newItems)
                    Log.d("VM", "Refresh successfully")
                }
            }

            override fun onFailure(call: Call<PopularMovies>, t: Throwable) {
                Log.d("VM", "Refresh failed")
            }

        })
    }

    fun loadTrailers(items: List<Item>) {
        Log.d("VM", "Loading trailers...")
        var count = 0
        for (item in items) {
            moviesRepo.getVideoKey(item.id, object : Callback<Trailers> {
                override fun onResponse(call: Call<Trailers>, response: Response<Trailers>) {
                    if (response.body() != null) {
                        val trailers = response.body()
                        trailersMap[item.id] = trailers!!
                        if (++count == items.size) {
                            Log.d("VM", "Load trailers completely")
                        }
                    }
                }

                override fun onFailure(call: Call<Trailers>, t: Throwable) {
                    count++
                    trailersMap[item.id]=null
                    Log.d("VM", "Load trailers of movie ${item.id} failed")
                }
            })
        }
    }

    fun getVideoKey(movie_id: Int): String? {
        while (!trailersMap.contains(movie_id)) {
            //waiting until trailers of movies loaded
            Log.d("VM", "Waiting for video_key...")
        }
        if (trailersMap[movie_id]==null || trailersMap[movie_id]!!.youtube.isEmpty()){
            return null
        }
        return trailersMap[movie_id]!!.youtube[0].source
    }
}