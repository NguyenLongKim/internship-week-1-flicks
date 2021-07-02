package com.example.flicks.api

import com.example.flicks.models.NowPlayingMovies
import com.example.flicks.models.Trailers
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDatabaseApi {
    @GET("/3/movie/now_playing")
    fun getMovies(@Query("api_key") api_key:String,@Query("page") page:Int): Call<NowPlayingMovies>

    @GET("/3/movie/{id}/trailers")
    fun getTrailers(@Path("id") id:Int,@Query("api_key") api_key: String):Call<Trailers>
}