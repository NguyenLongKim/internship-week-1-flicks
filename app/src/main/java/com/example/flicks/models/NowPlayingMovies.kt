package com.example.flicks.models

data class NowPlayingMovies(
    val dates: Dates,
    val page:Int,
    val results:List<Item>,
    val total_pages:Int,
    val total_results: Int
)

data class Dates(
    val maximum:String,
    val minimum:String
)