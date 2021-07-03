package com.example.flicks.repositories

import com.example.flicks.api.MovieDatabaseApi
import com.example.flicks.models.NowPlayingMovies
import com.example.flicks.models.PopularMovies
import com.example.flicks.models.Trailers
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MoviesRepository {
    companion object {
        var instance: MoviesRepository = MoviesRepository()
        private const val BASE_URL = "https://api.themoviedb.org"
        private const val api_key = "a07e22bc18f5cb106bfe4cc1f83ad8ed"
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private val apiService = retrofit.create(MovieDatabaseApi::class.java)
    }


    fun loadItems(page: Int, callBack: Callback<NowPlayingMovies>) {
        apiService.getMovies(api_key, page).enqueue(callBack)
    }

    fun loadPopularItems(page:Int,callBack: Callback<PopularMovies>){
        apiService.getPopularMovies(api_key,page).enqueue(callBack)
    }

    fun getVideoKey(movie_id: Int, callBack: Callback<Trailers>) {
        apiService.getTrailers(movie_id, api_key).enqueue(callBack)
    }
}